package com.samuilolegovich.servise.impl;

import com.samuilolegovich.model.EmailInfo;
import com.samuilolegovich.servise.EmailService;
import com.samuilolegovich.servise.MailContentBuilder;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.sendgrid.Method.POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {

    private final MailContentBuilder mailContentBuilder;
    private final SendGrid sendGrid;

    @Value("${email.sender}")
    private String senderEmail;


    @Override
    @SneakyThrows
    public void sendEmail(EmailInfo emailInfo) {

        log.info("send email invoked with emailInfo: {}", emailInfo);
        String body = mailContentBuilder.build(emailInfo.getText());

        Mail mail = new Mail(new Email(senderEmail),
                emailInfo.getSubject(), new Email(emailInfo.getSendTo()), new Content("text/html", body));

        Request request = new Request();
        request.setMethod(POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);

        log.info("Response code: {}, body: {} ", response.getStatusCode(), request.getBody());
    }
}
