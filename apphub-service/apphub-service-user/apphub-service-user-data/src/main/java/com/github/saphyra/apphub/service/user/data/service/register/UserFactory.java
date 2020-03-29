package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.encryption.impl.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class UserFactory {
    private final IdGenerator idGenerator;
    private final PasswordService passwordService;

    User create(String email, String username, String password) {
        return User.builder()
            .userId(idGenerator.randomUuid())
            .email(email.toLowerCase())
            .username(username)
            .password(passwordService.hashPassword(password))
            .build();
    }
}
