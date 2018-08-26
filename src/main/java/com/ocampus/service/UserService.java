package com.ocampus.service;

import com.ocampus.dao.LoginTicketDAO;
import com.ocampus.dao.UserDAO;
import com.ocampus.model.LoginTicket;
import com.ocampus.model.User;
import com.ocampus.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public Map<String,Object> register (String username, String email, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        if(StringUtils.isBlank(email)){
            map.put("msgemail", "邮箱不能为空");
            return map;
        }

        if(!ToutiaoUtil.isEmail(email)){
            map.put("msgemail", "请填写正确的邮箱");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        if(!ToutiaoUtil.checkPasswordStrength(password)){
            map.put("msgpwd", "密码强度过低");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        //String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        String head = "https://s19.postimg.cc/5cse0foq7/timg_4.jpg";
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());

        return map;
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String,Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        if(!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd", "密码不正确");
            return map;
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());

        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
