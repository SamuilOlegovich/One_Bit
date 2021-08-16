package com.samuilolegovich.service;

import com.samuilolegovich.dto.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface AccountService {
    AnswerEmailConfirmationDto confirmEmail(Long userId, String emailToken);

    AnswerToNewUserDto registerNewPlayer(NewUserDto newUserDto);

    AnswerSignInDto signIn(SignInDto signInDto);

    void logout(OAuth2Authentication auth);

    void blockAccount(Long userId);

    void blockAccount(Long userId, Long daysToBlock);

    void unblockAccount(Long userId);

    void forgotPassword(ForgotPasswordDto forgotPasswordDto);

    void resetPassword(NewPasswordInfo newPasswordInfo);

    void changePassword(ChangePasswordTokenDto changePasswordTokenDto);
}
