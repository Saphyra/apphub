package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserFactory {
    private final IdGenerator idGenerator;
    private final PasswordService passwordService;

    User create(String email, String username, String password, String locale) {
        return User.builder()
            .userId(idGenerator.randomUuid())
            .email(email.toLowerCase())
            .username(username)
            .password(passwordService.hashPassword(password))
            .language(locale)
            .passwordFailureCount(0)
            .build();
    }
}
