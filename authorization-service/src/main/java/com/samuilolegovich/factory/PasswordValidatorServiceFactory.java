package com.samuilolegovich.factory;

import com.google.common.collect.Sets;
import com.samuilolegovich.domain.Role;
import com.samuilolegovich.service.AuthPasswordValidator;
import com.samuilolegovich.service.impl.AdminPasswordValidatorImpl;
import com.samuilolegovich.service.impl.UserAuthPasswordValidator;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.samuilolegovich.domain.Role.*;
import static lombok.AccessLevel.PRIVATE;

// туи определяем какая роль у юзера и соответственно роли отдаем объект с параметрами проверки пароля по нужную роль
@NoArgsConstructor(access = PRIVATE)
public class PasswordValidatorServiceFactory {
    public static AuthPasswordValidator getValidatorForRole(Set<Role> roles) {
        Role userRole = roles.stream().findFirst().orElse(ROLE_GAMER);
        var adminRoles = Sets.newHashSet(ROLE_ADMIN, ROLE_OWNER);

        if (adminRoles.contains(userRole)) return new AdminPasswordValidatorImpl();
        return new UserAuthPasswordValidator();
    }
}
