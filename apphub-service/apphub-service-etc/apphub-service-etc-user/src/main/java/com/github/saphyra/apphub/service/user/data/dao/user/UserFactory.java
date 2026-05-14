package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.config.properties.RegistrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final IdGenerator idGenerator;
    private final PasswordService passwordService;
    private final RegistrationProperties registrationProperties;

    public User create(String email, String username, String password, String language) {
        UUID userId = idGenerator.randomUuid();
        return User.builder()
            .userId(userId)
            .email(email)
            .username(username)
            .password(passwordService.hashPassword(password, userId))
            .language(language)
            .roles(registrationProperties.getDefaultRoles())
            .build();
    }
}
