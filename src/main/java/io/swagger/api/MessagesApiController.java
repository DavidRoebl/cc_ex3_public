package io.swagger.api;

import io.swagger.model.*;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-08T23:11:14.377Z")

@Controller
public class MessagesApiController implements MessagesApi {

    public ResponseEntity<IdsWrapper> messagesGet(
            @ApiParam(value = "the authorization token of the sender" ,required=true)
            @RequestHeader(value="auth_token", required=true)
                    String authToken,
            @ApiParam(value = "filter all read/unread messages", allowableValues = "unread")
            @RequestParam(value = "status", required = false)
                    String status) {

        boolean unread = "unread".equals(status);
        DatabaseConnection dbC = DatabaseConnection.getInstance();

        List<Message> messages = null;
        try {
            messages = dbC.getMessages(authToken, unread);
        } catch (DatabaseConnection.UnauthorizedException e) {
            return new ResponseEntity<IdsWrapper>(HttpStatus.valueOf(400));
        }
        IdsWrapper idsWrapper = new IdsWrapper();
        for(Message message : messages){
            IdWrapper id = new IdWrapper();
            id.setId(message.getId());
            idsWrapper.addIdsItem(id);
        }

        return new ResponseEntity<IdsWrapper>(idsWrapper, HttpStatus.OK);
    }

    public ResponseEntity<Void> messagesIdDelete(
            @ApiParam(value = "the authorization token of the sender" ,required=true)
            @RequestHeader(value="auth_token", required=true)
                    String authToken,
            @ApiParam(value = "",required=true )
            @PathVariable("id")
                    Integer id) {

        DatabaseConnection dbC = DatabaseConnection.getInstance();

        try {
            dbC.deleteMessage(id, authToken);

        } catch (DatabaseConnection.UnauthorizedException e) {
            return new ResponseEntity<Void>(HttpStatus.valueOf(400));

        } catch (DatabaseConnection.ForbiddenException e){
            return new ResponseEntity<Void>(HttpStatus.valueOf(403));
        }

        return new ResponseEntity<Void>(HttpStatus.valueOf(204));
    }

    public ResponseEntity<Message> messagesIdGet(
            @ApiParam(value = "the authorization token of the sender" ,required=true)
            @RequestHeader(value="auth_token", required=true)
                    String authToken,
            @ApiParam(value = "",required=true )
            @PathVariable("id")
                    Integer id) {

        DatabaseConnection dbC = DatabaseConnection.getInstance();

        Message message = null;
        try {
            message = dbC.findMessage(id, authToken);

        } catch (DatabaseConnection.UnauthorizedException e) {
            return new ResponseEntity<Message>(HttpStatus.valueOf(400));

        } catch (DatabaseConnection.ForbiddenException e){
            return new ResponseEntity<Message>(HttpStatus.valueOf(403));
        }

        if(null == message){
            return new ResponseEntity<Message>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    public ResponseEntity<Message> messagesPost(
            @ApiParam(value = "the authorization token of the sender" ,required=true)
            @RequestHeader(value="auth_token", required=true)
                    String authToken,
            @ApiParam(value = "the message to send" ,required=true )
            @Valid @RequestBody
                    Message message) {

        DatabaseConnection dbC = DatabaseConnection.getInstance();

        Message result = null;
        try {
            result = dbC.createMessage(message, authToken);
        } catch (DatabaseConnection.UnauthorizedException e) {
            return new ResponseEntity<Message>(HttpStatus.valueOf(400));
        }

        return new ResponseEntity<Message>(result, HttpStatus.valueOf(201));
    }

}
