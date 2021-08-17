package com.samuilolegovich.enums;

import lombok.Getter;

public enum AccountStatusCode {
    // Аккаунт не активирован
    NEW_ACCOUNT("000", "Account is not activated"),
    // Аккаунт активен
    ACTIVE_ACCOUNT("001", "Account is active"),
    // Требуется действие по изменению пароля учетной записи
    PASSWORD_CHANGE_REQUIRED_CODE("002", "Account password change action required"),
    // Пароль учетной записи требует обновления из-за 60-дневной политики
    PASSWORD_UPDATE_REQUIRED("003", "Account password require update due to 60-days policy"),
    // Аккаунт заблокирован из-за слишком большого количества неправильных попыток входа
    BLOCKED_INCORRECT_ATTEMPTS("004", "Account is blocked by too many incorrect login attempts"),
    // Аккаунт заблокирован. Свяжитесь с администратором или подождите указанное время
    MANUAL_BLOCKED_ACCOUNT("005", "Account is blocked. Contact admin or wait specified time"),
    // Аккаунт заблокирован администратором
    ADMIN_BLOCKED_ACCOUNT("006", "Account is blocked by admin"),
    // Неверные учетные данные
    INVALID_CREDENTIALS("007", "Invalid credentials"),
    INVITED("008", ""),
    // Временный пароль пользователя устарел
    TEMP_PASSWORD_INVALID("011", "User temp password is out of date");


    @Getter
    String code;
    @Getter
    String message;

    AccountStatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
