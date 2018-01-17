package io.swagger.api;

import io.swagger.model.DatabaseConnection;
import io.swagger.model.User;
import io.swagger.model.Users;

import io.swagger.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import javax.validation.constraints.*;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-08T23:11:14.377Z")

@Controller
public class UserApiController implements UserApi {

    private static final Logger LOGGER = LoggerFactory.getLogger("UserApiController");

    public ResponseEntity<Users> userGet() {
        // do some magic!
        DatabaseConnection dbC = DatabaseConnection.getInstance();
        List<User> users = dbC.getUsers();

        Users users_obj = new Users();
        for(User user : users){
            user.setToken("");
            users_obj.add(user);
        }

        return new ResponseEntity<Users>(users_obj, HttpStatus.OK);
    }

    public ResponseEntity<User> userPost(
            @ApiParam(value = "the user to create" ,required=true )
            @Valid @RequestBody
                    User user) {

        DatabaseConnection dbC = DatabaseConnection.getInstance();
        LOGGER.info("userPost: data base connection = " + dbC);

        User result = dbC.createUser(user);
        LOGGER.info("userPost: result = " + user);

        return new ResponseEntity<User>(result, HttpStatus.OK);
    }

}
