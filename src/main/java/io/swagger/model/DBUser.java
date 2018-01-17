package io.swagger.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "dbuser")
public class DBUser {
    private static final Logger LOGGER = LoggerFactory.getLogger("DBUser");
    private String name = null;

    @Id
    @SequenceGenerator(name = "uidGenerator", sequenceName = "uidsequence")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "uidGenerator")
    private Integer userid;

    private String token;

    public static DBUser createUser(User user){
        DBUser dbUser = new DBUser();
        dbUser.setName(user.getName());
        double random = Math.random();
        LOGGER.info("random = " + random);
        int randomInt = (int)(Integer.MAX_VALUE * random);
        LOGGER.info("randomInt = " + randomInt);
        String curToken = String.format("%X", randomInt);
        LOGGER.info("curToken = " + curToken);
        dbUser.setToken(curToken);
        return dbUser;
    }

    public static DBUser fromUser(User user){
        return new DBUser(user.getName(), user.getId(), user.getToken());
    }

    public User toUser(){
        User user = new User();
        user.setId(userid);
        user.setToken(token);
        user.setName(name);
        return user;
    }

    public DBUser() {}

    public DBUser(String name, Integer id, String token){
        this.name = name;
        this.userid = id;
        this.token = token;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return userid;
    }
    public void setId(Integer id) {
        this.userid = id;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}

