package com.samuilolegovich.listener;

import com.samuilolegovich.config.AmqpConfiguration;
import com.samuilolegovich.model.EmailInfo;
import com.samuilolegovich.servise.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final EmailService emailService;



    @RabbitListener(queues = AmqpConfiguration.EMAIL_QUEUE)
    public void getMessage(EmailInfo emailInfo) {
        log.info("Message received");

        emailService.sendEmail(emailInfo);

        log.info("Listener executed");
    }
}
