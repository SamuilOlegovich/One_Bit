package com.samuilolegovich.model;

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
    @NotNull
    private String sendTo;
    @NotNull
    private String subject;
    @NotNull
    private String text;
    private Map<String, Object> additionalInfo;
}
