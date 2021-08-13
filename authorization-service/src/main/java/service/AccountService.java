package service;

import com.samuilolegovich.dto.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {
    AnswerEmailConfirmationDto confirmEmail(String emailToken);

    AnswerToNewUserDto registerNewPlayer(NewUserDto newUserDto);

    AnswerSignInDto signIn(SignInDto signInDto);

    void logout(OAuth2Authentication auth);

    @Transactional
    void changePassword(ChangePasswordTokenDto changePasswordTokenDto);
}
