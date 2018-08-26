package com.ocampus.async.handler;

import com.ocampus.async.EventHandler;
import com.ocampus.async.EventModel;
import com.ocampus.async.EventType;
import com.ocampus.model.Message;
import com.ocampus.model.User;
import com.ocampus.service.MessageService;
import com.ocampus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CommentHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + user.getName() +
                " 评论了你的资讯,http://127.0.0.1:8080/news/"
                + String.valueOf(model.getEntityId()));

        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId(String.format("%d_%d", 3, model.getEntityOwnerId()));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}

