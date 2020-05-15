package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.ConflictException;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
//TODO unit test
public class UsernameValidator {
    private final UserDao userDao;

    public void validateUsername(String username) {
        if (isNull(username)) {
            throw ExceptionUtil.wrongPayloadException("username");
        }

        if (username.length() < 3) {
            throw ExceptionUtil.invalidParamException(ErrorCode.USERNAME_TOO_SHORT, "Username too short.");
        }

        if (username.length() > 30) {
            throw ExceptionUtil.invalidParamException(ErrorCode.USERNAME_TOO_LONG, "Username too long.");
        }

        if (userDao.findByUsername(username).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.USERNAME_ALREADY_EXISTS.name()), "Username already exists");
        }
    }
}
