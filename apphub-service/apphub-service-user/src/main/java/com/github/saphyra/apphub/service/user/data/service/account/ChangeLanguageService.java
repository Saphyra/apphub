package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChangeLanguageService {
    private final CommonConfigProperties commonConfigProperties;
    private final UserDao userDao;

    public void changeLanguage(UUID userId, String language) {
        if (isNull(language)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "language is null"), "Language must not be null.");
        }
        if (!commonConfigProperties.getSupportedLocales().contains(language)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "value", "language not supported"), String.format("Language %s is not supported.", language));
        }

        User user = userDao.findById(userId);
        user.setLanguage(language);
        userDao.save(user);
    }
}
