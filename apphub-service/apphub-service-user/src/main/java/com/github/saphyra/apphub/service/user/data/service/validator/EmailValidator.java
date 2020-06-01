package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.ConflictException;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final UserDao userDao;

    public void validateEmail(String email) {
        if (isNull(email)) {
            throw ExceptionUtil.wrongPayloadException("email");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ExceptionUtil.wrongPayloadException("email", "invalid format");
        }

        if (userDao.findByEmail(email.toLowerCase()).isPresent()) {
            throw new ConflictException(new ErrorMessage(ErrorCode.EMAIL_ALREADY_EXISTS.name()), "Email already exists.");
        }
    }
}
