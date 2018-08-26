package com.ocampus.service;

import com.ocampus.dao.NewsDAO;
import com.ocampus.model.News;
import com.ocampus.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    public News getNewsById(int id){
        return newsDAO.selectById(id);
    }

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public String saveImage(MultipartFile file) throws IOException{
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if(dotPos < 0){
            return null;
        }
        String fileSuffix = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if(!ToutiaoUtil.isImage(fileSuffix)){
            return null;
        }
        //防止重名
        String fileName = UUID.randomUUID().toString().replaceAll("-","") + "." + fileSuffix;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image/?name=" + fileName;

    }

    public int addNews(News news) {

        return newsDAO.addNews(news);
    }

    public int updateCommentCount(int id, int commentCount){
        return newsDAO.updateCommentCount(id, commentCount);
    }

    public int updateLikeCount(int id, int likeCount){
        return newsDAO.updateLikeCount(id, likeCount);
    }
}
