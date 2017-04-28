package com.fct.finance.service;


import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by nick on 2017/4/3.
 * @author nick
 */
@SpringBootApplication(scanBasePackages = "com.fct")
@ImportResource("classpath:dubbo/dubbo-consumer.xml")
public class ApplicationStartUp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationStartUp.class);
    }

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(ApplicationStartUp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
