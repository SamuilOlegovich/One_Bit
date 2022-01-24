package com.samuilolegovich.service;

import com.samuilolegovich.dto.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface AccountService {
    AnswerEmailConfirmationDto confirmEmail(String emailToken);

    AnswerToNewUserDto registerNewPlayer(NewUserDto newUserDto);

    AnswerSignInDto signIn(SignInDto signInDto);

    void logout(OAuth2Authentication auth);

    void changePassword(ChangePasswordTokenDto changePasswordTokenDto);
}
