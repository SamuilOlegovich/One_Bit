package com.samuilolegovich.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSignInDto {
    private String errorMessage;
    private String message;
}
