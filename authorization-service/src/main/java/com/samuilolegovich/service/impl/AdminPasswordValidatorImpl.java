package com.samuilolegovich.service.impl;

import com.samuilolegovich.service.AuthPasswordValidator;
import org.passay.*;

// Тут задаем параметры какой должен быть логин для администраторов и проверяем его
public class AdminPasswordValidatorImpl implements AuthPasswordValidator {
    @Override
    public RuleResult validate(String password, String username) {

        LengthRule lengthRule = new LengthRule();
        lengthRule.setMinimumLength(15);

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
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 15, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 15, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 15, false),
                new WhitespaceRule());

        return passwordValidator.validate(new PasswordData(password));
    }
}
