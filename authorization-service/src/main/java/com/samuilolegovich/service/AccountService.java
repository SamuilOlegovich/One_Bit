package com.samuilolegovich.service;

import com.samuilolegovich.dto.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface AccountService {
    void confirmEmail(String userEmail, String emailToken);

    void changePassword(ChangePasswordTokenDto changePasswordTokenDto);

    void forgotPassword(ForgotPasswordDto forgotPasswordDto);

    void resetPassword(NewPasswordInfo newPasswordInfo);

    void blockAccount(Long userId, Long daysToBlock);

    void registerNewPlayer(NewUserDto newUserDto);

    OAuth2AccessToken signIn(SignInDto signInDto);

    void logout(OAuth2Authentication auth);

    void unblockAccount(Long userId);

    void blockAccount(Long userId);
}
