package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckPasswordService {
    private final PasswordService passwordService;
    private final UserDao userDao;

    public void checkPassword(UUID userId, String password) {
        String hash = userDao.findByIdValidated(userId)
            .getPassword();

        if (!passwordService.authenticate(password, hash)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.BAD_PASSWORD.name()), "Bad password");
        }
    }
}
