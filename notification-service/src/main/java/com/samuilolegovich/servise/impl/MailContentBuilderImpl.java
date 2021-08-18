package com.samuilolegovich.servise.impl;

import com.samuilolegovich.servise.MailContentBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailContentBuilderImpl implements MailContentBuilder {

    private final TemplateEngine templateEngine;


    @Override
    public String build(String body) {
        Context context = new Context();
        context.setVariable("text", body);

        return templateEngine.process("mailTemplate.html", context);
    }
}
