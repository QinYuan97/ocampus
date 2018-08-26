package com.ocampus.async.handler;


import com.ocampus.async.EventHandler;
import com.ocampus.async.EventModel;
import com.ocampus.async.EventType;
import com.ocampus.controller.LoginController;
import com.ocampus.model.Message;
import com.ocampus.model.User;
import com.ocampus.service.MessageService;
import com.ocampus.service.UserService;
import com.ocampus.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RegisterHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {


        //发送邮件通知
        Map<String, Object> map = new HashMap();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("to"), "欢迎来到OCampus",
                "mails/welcome.html", map);

        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getActorId());
        message.setContent("欢迎来到Ocampus!");
        // SYSTEM ACCOUNT
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId(String.format("%d_%d", 3, model.getEntityOwnerId()));
        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
