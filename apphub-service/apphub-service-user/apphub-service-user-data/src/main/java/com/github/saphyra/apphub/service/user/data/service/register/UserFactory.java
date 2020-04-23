package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.encryption.impl.PasswordService;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserFactory {
    private final IdGenerator idGenerator;
    private final PasswordService passwordService;

    User create(String email, String username, String password, String locale) {
        return User.builder()
            .userId(idGenerator.randomUUID())
            .email(email.toLowerCase())
            .username(username)
            .password(passwordService.hashPassword(password))
            .language(locale)
            .build();
    }
}
