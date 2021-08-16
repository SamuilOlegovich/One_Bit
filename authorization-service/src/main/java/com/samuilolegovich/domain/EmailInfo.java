package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailInfo implements Serializable {
    private String replyChannel;

    @NotNull
    private String sendTo;
    @NotNull
    private String subject;
    @NotNull
    private String text;
    private Map<String, Object> additionalInfo;

    public static final String EMAIL_EXCHANGE = "email";
    public static final String EMAIL_ROUTING_KEY = "email-publish";
}
