package com.imooc;

import com.imooc.controller.interceptor.MiniInteceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig  extends WebMvcConfigurerAdapter {

    @Value("${fileSpace}")
    private String fileSpace;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:"+fileSpace);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInteceptor()).addPathPatterns("/user/**").addPathPatterns("/video/upload", "/video/uploadCover",
                "/video/userLike", "/video/userUnLike",
                "/video/saveComment")
                .addPathPatterns("/bgm/**")
                .excludePathPatterns("/user/queryPublisher");
        super.addInterceptors(registry);
    }

    @Bean
    public MiniInteceptor miniInteceptor(){
        return new MiniInteceptor();
    }

}
