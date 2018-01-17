package io.swagger.model;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dbmessage")
public class DBMessage {
    @ManyToMany
    @JoinTable(
            name="MESSAGE_RECIPIENT_USERS",
            joinColumns = @JoinColumn(name="msgid"),
            inverseJoinColumns = @JoinColumn(name="userid")
    )
    private List<DBUser> recipients = new ArrayList<DBUser>();

    private String message = null;

    @Id
    @SequenceGenerator(name = "midGenerator", sequenceName = "midsequence")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "midGenerator")
    private Integer msgid = null;

    @ManyToOne
    @JoinColumn
    private DBUser sender = null;

    @ManyToMany
    @JoinTable(
            name="MESSAGE_READ_BY_USERS",
            joinColumns = @JoinColumn(name="msgid"),
            inverseJoinColumns = @JoinColumn(name="userid")
    )
    private List<DBUser> readBy = new ArrayList<DBUser>();

    public static DBMessage createMessage(Message message){
        message.setReadBy(new ArrayList<IdWrapper>());
        return fromMessage(message);
    }

    public static DBMessage fromMessage(Message message){
        DatabaseConnection dbC = DatabaseConnection.getInstance();
        List<DBUser> users = dbC.getDBUsers();
        List<DBUser> recipients = new ArrayList<>();
        List<DBUser> readBy = new ArrayList<>();
        DBUser sender = null;
        for(DBUser user : users){
            IdWrapper recipient = new IdWrapper();
            recipient.setId(user.getId());
            if(message.getRecipients().contains(recipient)){
                recipients.add(user);
            }
            if(message.getReadBy().contains(recipient)){
                readBy.add(user);
            }
            if(user.getId().equals(message.getSender())){
                sender = user;
            }
        }

        return new DBMessage(recipients, message.getMessage(), message.getId(), sender, readBy);
    }

    public Message toMessage(){
        Message webMessage = new Message();
        webMessage.setId(msgid);
        webMessage.setMessage(message);
        webMessage.setSender(sender.getId());
        List<IdWrapper> idWrapperList = new ArrayList<IdWrapper>();
        for(DBUser recipient : recipients){
            IdWrapper idWrapper = new IdWrapper();
            idWrapper.setId(recipient.getId());
            idWrapperList.add(idWrapper);
        }
        webMessage.setRecipients(idWrapperList);

        idWrapperList = new ArrayList<>();
        for(DBUser readByUser : readBy){
            IdWrapper idWrapper = new IdWrapper();
            idWrapper.setId(readByUser.getId());
            idWrapperList.add(idWrapper);
        }
        webMessage.setReadBy(idWrapperList);
        return webMessage;
    }

    public DBMessage(){}

    public DBMessage(List<DBUser> recipients, String message, Integer id, DBUser sender, List<DBUser> readBy){
        this.recipients = recipients;
        this.message = message;
        this.msgid = id;
        this.sender = sender;
        this.readBy = readBy;
    }

    public List<DBUser> getRecipients() {
        return recipients;
    }
    public void setRecipients(List<DBUser> recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return msgid;
    }
    public void setId(Integer id) {
        this.msgid = id;
    }

    public DBUser getSender() {
        return sender;
    }
    public void setSender(DBUser sender) {
        this.sender = sender;
    }

    public List<DBUser> getReadBy() {
        return readBy;
    }
    public void setReadBy(List<DBUser> readBy) {
        this.readBy = readBy;
    }
}

