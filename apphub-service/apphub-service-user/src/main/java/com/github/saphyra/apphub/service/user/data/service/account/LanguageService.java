package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
public class LanguageService {
    private final CommonConfigProperties commonConfigProperties;
    private final UserDao userDao;

    public void changeLanguage(UUID userId, String language) {
        if (isNull(language)) {
            throw ExceptionFactory.invalidParam("value", "must not be null");
        }
        if (!commonConfigProperties.getSupportedLocales().contains(language)) {
            throw ExceptionFactory.invalidParam("value", "not supported");
        }

        User user = userDao.findByIdValidated(userId);
        user.setLanguage(language);
        userDao.save(user);
    }

    public String getLanguage(UUID userId) {
        return userDao.findByIdValidated(userId).getLanguage();
    }
}
