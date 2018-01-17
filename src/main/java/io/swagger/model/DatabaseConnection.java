package io.swagger.model;

import io.swagger.DatabaseConfig;
import org.hibernate.*;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 12/9/2017.
 */
public class DatabaseConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger("DatabaseConnection");

    private static DatabaseConnection sInstance;
    private SessionFactory sessionFactory;

    private DatabaseConnection(){

        DatabaseConfig dbConfig = new DatabaseConfig();
        LocalSessionFactoryBean lsfb = dbConfig.sessionFactory();
        sessionFactory = lsfb.getObject();

        LOGGER.info("constructor: sessionFactory = " + sessionFactory);
    }

    public static DatabaseConnection getInstance(){
        if(null == sInstance){
            sInstance = new DatabaseConnection();
        }
        return sInstance;
    }

    /**
     * creates a new user in the database
     * @param user  specification of the user to create (all dropped except name field)
     * @return      the newly created DBUser
     */
    public DBUser createDBUser(User user){
        Session session = sessionFactory.openSession();
        LOGGER.info("createUser: session = " + session);
        LOGGER.info("createUser: user = " + user);

        Transaction tx = null;
        DBUser dbUser = null;
        try {
            tx = session.beginTransaction();
            LOGGER.info("createUser: transaction = " + tx);

            dbUser = DBUser.createUser(user);
            LOGGER.info("createUser: dbUser = " + dbUser);
            session.save(dbUser);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } catch (Exception e){
            //evil, i know
            System.err.println("caught unknown exception");
            e.printStackTrace();
        } finally {
            session.close();
        }
        return dbUser;
    }

    /**
     * creates a new user in the database
     * @param user  specification of the user to create (all dropped except name field)
     * @return      the newly created user
     */
    public User createUser(User user){
        DBUser dbUser = createDBUser(user);
       return null != dbUser ? dbUser.toUser() : null;
    }

    /**
     * gets all DBUsers from database
     * @return list of all DBUsers in database
     */
    public List<DBUser> getDBUsers(){
        Session session = sessionFactory.openSession();
        List<DBUser> users = getDBUsers(session);

        session.close();
        return users;
    }

    /**
     * gets all users from database
     * @return list of all users in database
     */
    public List<User> getUsers(){
        List<DBUser> dbResult = getDBUsers();
        List<User> retVal = new ArrayList<User>();
        for(DBUser dbUser: dbResult){
            retVal.add(dbUser.toUser());
        }

        return retVal;
    }

    /**
     * creates a new message in the database
     * @param message   the message passed in the web api call
     * @param authToken the auth token of the user creating the message
     * @return          the created message object
     * @throws UnauthorizedException if there is no user who owns the auth token
     */
    public Message createMessage(Message message, String authToken) throws UnauthorizedException{

        Session session = sessionFactory.openSession();
        LOGGER.info("createMessage: session = " + session);

        DBUser user = getDBUser(session, authToken);
        if(null == user){
            session.close();
            throw new UnauthorizedException();
        }
        message.setSender(user.getId());

        Transaction tx = null;
        DBMessage dbMessage = null;
        try {
            tx = session.beginTransaction();
            LOGGER.info("createMessage: transaction = " + tx);

            dbMessage = DBMessage.createMessage(message);
            LOGGER.info("createMessage: dbMessage = " + dbMessage);

            session.save(dbMessage);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } catch (Exception e){
            //evil, i know
            LOGGER.error("caught unknown exception");
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null != dbMessage ? dbMessage.toMessage() : null;
    }

    /**
     *
     * @param authToken the authorization token of the user
     * @param unread    filter messages for unread (unread==true: only return unread messages; unread==false: return all messages)
     * @return          all (unread) messages for user
     * @throws UnauthorizedException if there is no user who owns the auth token
     */
    public List<Message> getMessages(String authToken, boolean unread) throws UnauthorizedException{
        Session session = sessionFactory.openSession();
        DBUser user = getDBUser(session, authToken);
        if(null == user){
            session.close();
            throw new UnauthorizedException();
        }

        List<Message> messageList = new ArrayList<>();
        List<DBMessage> messages = getDBMessages(session);
        for(DBMessage message : messages){
            if(message.getRecipients().contains(user)){
                // 1) check unread flag
                //      false --> return message
                //      true  --> 2)
                // 2) check if message was already read by this user
                //      false --> return message
                //      true  --> skip this message
                if(!unread || !message.getReadBy().contains(user)){
                    messageList.add(message.toMessage());
                }
            }
        }
        session.close();
        return messageList;
    }

    /**
     * deletes message from database if not read by any recipient
     * @param id        the id of the message to delete
     * @param authToken the authorization of the user requesting deletion
     * @throws UnauthorizedException if the authorization is not valid
     * @throws ForbiddenException    if the message was already read or if this message does not belong to the requesting user
     */
    public void deleteMessage(int id, String authToken) throws UnauthorizedException, ForbiddenException{
        Session session = sessionFactory.openSession();
        DBUser user = getDBUser(session, authToken);

        if(null == user) {
            session.close();
            throw new UnauthorizedException();
        }

        boolean foundMessage = false;
        Transaction tx = session.beginTransaction();
        List<DBMessage> messages = getDBMessages(session);
        for(DBMessage message : messages){
            if(message.getSender().equals(user) && message.getId().equals(id)){
                if(message.getReadBy().size() == 0){
                    //message read by none of the recipients --> OK to delete
                    session.delete(message);
                    LOGGER.info("successfully deleted message : " + message);
                    foundMessage = true;
                    break;

                } else {
                    //message already read by some useres --> UNABLE TO DELETE!!
                    LOGGER.info("unable to delete message : " + message + "cause: read by people");
                    tx.rollback();
                    session.close();
                    throw new ForbiddenException();
                }
            }
        }

        if(!foundMessage){
            // no message found for the specified id that was sent by user
            LOGGER.info("no message found that matches id:" + id + " and sender:" + user);
            tx.rollback();
            session.close();
            throw new ForbiddenException();
        }

        tx.commit();
        session.close();
    }

    /**
     * finds the message denoted by id and sets the user denoted by auth token in the read-by field of the message
     * @param id        the id of the message to find
     * @param authToken the authorization of the user who is accessing
     * @return          the message
     * @throws UnauthorizedException if there is no user who owns auth token
     * @throws ForbiddenException    if the message does not belong to user
     */
    public Message findMessage(int id, String authToken) throws UnauthorizedException, ForbiddenException{
        Session session = sessionFactory.openSession();
        DBUser user = getDBUser(session, authToken);
        if(null == user){
            session.close();
            throw new UnauthorizedException();
        }

        Message retMessage = null;
        List<DBMessage> messages = getDBMessages(session);
        for (DBMessage message : messages) {
            if (message.getRecipients().contains(user) && message.getId() == id) {
                updateReadBy(session, message, user);
                retMessage = message.toMessage();
                break;
            }
        }

        session.close();
        return retMessage;
    }

    /**
     * gets all users from database
     * @param session   the currently open database session
     * @return          list f all users in database
     */
    private List<DBUser> getDBUsers(Session session){
        Query hqlQuery = session.createQuery("from DBUser");
        List result = hqlQuery.list();
        return result;
    }

    /**
     * gets all messages from database
     * @param session   the currently open database session
     * @return          list of all messages in database
     */
    private List<DBMessage> getDBMessages(Session session){
        Query hqlQuery = session.createQuery("from DBMessage");
        List result = hqlQuery.list();
        return result;
    }

    /**
     * updates the messages read by list by adding the DBUser "reader"
     * @param session   the currently open database session
     * @param message   the message to update
     * @param reader    the DBUser who reads the message
     */
    private void updateReadBy(Session session, DBMessage message, DBUser reader){
        LOGGER.info("createMessage: session = " + session);

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List<DBUser> readBy = message.getReadBy();
            readBy.add(reader);
            message.setReadBy(readBy);

            session.update(message);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } catch (Exception e){
            //evil, i know
            LOGGER.error("caught unknown exception");
            e.printStackTrace();
        }
    }

    /**
     * gets a DBUser from the database
     * @param session   the currently open database session
     * @param authToken the authorization of the user to get
     * @return          the DBUser specified by authtoken
     */
    private DBUser getDBUser(Session session, String authToken){
        List<DBUser> users = getDBUsers(session);
        for(DBUser curUser : users){
            if(curUser.getToken().equals(authToken)){
                return curUser;
            }
        }
        return null;
    }

    /**
     * Exception to show lack of authorization to perform requested action
     * auth token was invalid
     */
    public class UnauthorizedException extends Exception{};

    /**
     * Exception to show that the requested action cannot be performed
     */
    public class ForbiddenException extends Exception{};
}
