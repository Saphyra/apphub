package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.service.user.data.service.ExceptionUtil;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class PasswordValidator {
    public void validatePassword(String password) {
        validatePassword(password, "password");
    }

    public void validatePassword(String password, String fieldName) {
        if (isNull(password)) {
            throw ExceptionUtil.wrongPayloadException(fieldName);
        }

        if (password.length() < 6) {
            throw ExceptionUtil.invalidParamException(ErrorCode.PASSWORD_TOO_SHORT, "Password is too short.");
        }

        if (password.length() > 30) {
            throw ExceptionUtil.invalidParamException(ErrorCode.PASSWORD_TOO_LONG, "Password is too long.");
        }
    }
}
