package com.ocampus.controller;


import com.ocampus.async.EventProducer;
import com.ocampus.model.*;
import com.ocampus.service.FollowService;
import com.ocampus.service.NewsService;
import com.ocampus.service.UserService;
import com.ocampus.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/follow"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("followedId") int followedId){

        User user = hostHolder.getUser();
        if(user == null){
            return ToutiaoUtil.getJSONString(1, "请先登录");
        }
        User followedUser = userService.getUser(followedId);
        if(followedUser == null){
            return ToutiaoUtil.getJSONString(1, "该用户不存在");
        }
        int userId = user.getId();
        if(!followService.follow(userId, followedId)){
            return ToutiaoUtil.getJSONString(1, "您已经关注过该用户");
        }

        //eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(userId).setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0, "关注成功");
    }

    @RequestMapping(path = {"/followList/{userId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("userId") int userId, Model model) {
        try{
            Set<Integer> userSet = followService.getFollow(userId);
            List<ViewObject> vos = new ArrayList<>();
            for(int id : userSet){
                ViewObject vo = new ViewObject();
                vo.set("id", userId);
                vo.set("name", userService.getUser(id).getName());
                vo.set("headUrl", userService.getUser(id).getHeadUrl());
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
        } catch (Exception e){
            logger.error("获取关注列表错误" + e.getMessage());
        }
        return "followList";

    }
}
