package com.samuilolegovich.service.impl;

import com.samuilolegovich.service.AuthPasswordValidator;
import org.passay.*;

// Тут задаем параметры какой должен быть логин для юзера и проверяем его
public class UserAuthPasswordValidator implements AuthPasswordValidator {
    @Override
    public RuleResult validate(String password, String username) {

        LengthRule lengthRule = new LengthRule();
        lengthRule.setMinimumLength(10);

        CharacterCharacteristicsRule characteristicsRule = new CharacterCharacteristicsRule();
        characteristicsRule.setNumberOfCharacteristics(3);

        characteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1));
        characteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Digit, 1));
        characteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        characteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, 1));

        UsernameRule usernameRule = new UsernameRule();

        PasswordValidator passwordValidator = new PasswordValidator(
                lengthRule,
                characteristicsRule,
                usernameRule,
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 10, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 10, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 10, false),
                new WhitespaceRule());

        PasswordData passwordData = new PasswordData(password);
        String usernameWithoutEmailDomain = username.substring(0, username.indexOf("@"));
        passwordData.setUsername(usernameWithoutEmailDomain);

        RuleResult validationResult = passwordValidator.validate(passwordData);
        return validationResult;
    }
}
