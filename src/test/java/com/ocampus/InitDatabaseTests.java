package com.ocampus;

import com.ocampus.dao.CommentDAO;
import com.ocampus.dao.LoginTicketDAO;
import com.ocampus.dao.NewsDAO;
import com.ocampus.dao.UserDAO;
import com.ocampus.model.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@WebAppConfiguration

@Sql({"/init-schema.sql"})
public class InitDatabaseTests {

    @Autowired
    NewsDAO newsDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    CommentDAO commentDAO;

    @Test
    public void InitDataBase() {
        Random r = new Random();
        News news = new News();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setName(String.format("USER%d", i));
            user.setHeadUrl(String.format("http://images.ocampus.com/head/%dt.png", r.nextInt(1000)));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.ocampus.com/head/%dm.png", r.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setLink(String.format("http://www.ocampus.com/link/{%d}.html", i));
            news.setTitle(String.format("Title {%d} ", i));
            news.setUserId(i+1);
            newsDAO.addNews(news);
            System.out.println(news.getId());


            for(int j = 0; j < 3; ++j) {
                Comment comment = new Comment();
                comment.setUserId(i+1);
                comment.setCreatedDate(new Date());
                comment.setStatus(0);
                comment.setContent("这是一个评论！" + String.valueOf(j));
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                commentDAO.addComment(comment);
            }

            user.setPassword("newpassword");
            userDAO.updatePassword(user);

            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i+1));
            loginTicketDAO.addTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(), 2);
        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));
    }
}
