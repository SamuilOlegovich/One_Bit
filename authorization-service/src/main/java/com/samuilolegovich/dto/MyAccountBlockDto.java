package com.samuilolegovich.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Модель данных содержит количество дней для самостоятельной блокировки учетной записи.")
public class MyAccountBlockDto {
    @NotNull
    @Positive
    private Long daysToBlock;
}
