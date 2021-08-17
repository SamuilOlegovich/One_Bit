package com.samuilolegovich.utils;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import static org.passay.EnglishCharacterData.LowerCase;
import static org.passay.EnglishCharacterData.UpperCase;

public class UtilTokenGenerator {

    public static String generateTokenFoMailActivation() {
        int passwordLength = 77 ;
        PasswordGenerator passwordGenerator = new PasswordGenerator();

        return passwordGenerator.generatePassword(passwordLength, new CharacterRule(UpperCase, 1),
                new CharacterRule(LowerCase),
                new CharacterRule(EnglishCharacterData.Digit));
    }
}
