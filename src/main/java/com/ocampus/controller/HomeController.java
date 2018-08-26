package com.ocampus.controller;

import com.ocampus.model.EntityType;
import com.ocampus.model.HostHolder;
import com.ocampus.model.News;
import com.ocampus.model.ViewObject;
import com.ocampus.service.LikeService;
import com.ocampus.service.NewsService;
import com.ocampus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(@RequestParam(value = "userId", defaultValue = "0") int userId,
                        @RequestParam(value = "pop", defaultValue = "0") int pop,
                        @RequestParam(value = "page", defaultValue = "1") int page,
                        Model model) {
        int limit = 20;
        int offset = (page - 1) * limit;
        model.addAttribute("vos", getNews(0, offset, limit));
        model.addAttribute("pop", pop);
        model.addAttribute("page", page);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(@PathVariable("userId") int userId,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            Model model) {
        int limit = 20;
        int offset = (page - 1) * limit;
        model.addAttribute("vos", getNews(userId, offset, limit));
        return "home";
    }

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }
}
