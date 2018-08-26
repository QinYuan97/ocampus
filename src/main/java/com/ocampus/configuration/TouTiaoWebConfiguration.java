package com.ocampus.configuration;

import com.ocampus.interceptor.LoginRequiredInterceptor;
import com.ocampus.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class TouTiaoWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);

        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/msg/addMessage", "/like", "/dislike", "/logout/", "/uploadImage/",
                "/user/addNews/",  "/addComment/");
        super.addInterceptors(registry);
    }
}
