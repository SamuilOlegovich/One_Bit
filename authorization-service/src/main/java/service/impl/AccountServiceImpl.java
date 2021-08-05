package service.impl;

import com.samuilolegovich.dto.*;
import com.samuilolegovich.service.LoggingDBService;
import lombok.AllArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import service.AccountService;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final ConsumerTokenServices tokenService;
    private final LoggingDBService logingDBService;
    private final TokenStore tokenStore;

    @Override
    public AnswerEmailConfirmationDto confirmEmail(String emailToken) {
        return ;
    }

    @Override
    public AnswerToNewUserDto registerNewPlayer(NewUserDto newUserDto) {
        return ;
    }

    @Override
    public AnswerSignInDto signIn(SignInDto signInDto) {
        return ;
    }

    @Override
    public void logout(OAuth2Authentication authentication) {
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        final OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        tokenService.revokeToken(accessToken.getValue());
        logingDBService.logDbMessage("logout successfully", "Logout", Level.INFO);
    }
}
