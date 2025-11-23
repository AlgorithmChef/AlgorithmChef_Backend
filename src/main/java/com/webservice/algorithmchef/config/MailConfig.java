package com.webservice.algorithmchef.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // 실제 SMTP 설정 없이 빈만 등록하는 임시 설정
        return new JavaMailSenderImpl();
    }
}
