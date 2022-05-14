package com.samuilolegovich.servise;


import com.samuilolegovich.model.EmailInfo;

public interface EmailService {
    void sendEmail(EmailInfo emailInfo);
}
