package com.yinrj.emos.wx.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/16
 */
@Component
@Scope("prototype")
public class EmailTask implements Serializable {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${emos.email.system}")
    private String mail;

    @Async
    public void sendAsync(SimpleMailMessage message) {
        message.setFrom(mail);
        message.setCc(mail);
        javaMailSender.send(message);
    }
}
