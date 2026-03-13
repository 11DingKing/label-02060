package com.webrtc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.webrtc.mapper")
public class WebrtcServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebrtcServiceApplication.class, args);
    }
}
