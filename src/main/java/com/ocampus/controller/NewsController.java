package com.ocampus.controller;


import com.ocampus.async.EventModel;
import com.ocampus.async.EventProducer;
import com.ocampus.async.EventType;
import com.ocampus.model.*;
import com.ocampus.service.CommentService;
import com.ocampus.service.LikeService;
import com.ocampus.service.NewsService;
import com.ocampus.service.UserService;
import com.ocampus.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try{
            News news = newsService.getNewsById(newsId);
            if(news != null){
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
                if (localUserId != 0) {
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
                } else {
                    model.addAttribute("like", 0);
                }
                //评论
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<>();
                for(Comment comment : comments){
                    ViewObject viewObject = new ViewObject();
                    viewObject.set("comment", comment);
                    viewObject.set("user",userService.getUser(comment.getUserId()));
                    commentVOs.add(viewObject);
                }
                model.addAttribute("comments", commentVOs);
            }
            model.addAttribute("news",news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e){
            logger.error("获取资讯明细错误" + e.getMessage());
        }
        return "detail";

    }
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam(value = "file") MultipartFile  file){
        try{
            String fileUrl = newsService.saveImage(file);
            if(fileUrl == null){
                return ToutiaoUtil.getJSONString(1, "图片上传失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e){
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/image/"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String  imageName,
                         HttpServletResponse response){
        try{
            response.setContentType("image");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)),
                                response.getOutputStream());
            }
         catch (Exception e){
            logger.error("读取图片失败" +imageName + e.getMessage());
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link,
                          @RequestParam("summary") String summary){
        try{
            News news = new News();
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setLink(link);
            news.setSummary(summary);
            news.setTitle(title);
            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            } else{
                //设置一个匿名用户
                news.setUserId(99);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e){
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加资讯失败");
        }
    }

    @RequestMapping(path = {"/addComment/"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int  newsId,
                          @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setCreatedDate(new Date());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setStatus(0);
            commentService.addComment(comment);
            News news = newsService.getNewsById(newsId);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e){
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }
}
