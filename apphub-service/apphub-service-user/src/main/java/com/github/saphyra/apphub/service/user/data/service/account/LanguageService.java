package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<LanguageResponse> getLanguages(UUID userId) {
        String currentLocale = getLanguage(userId);
        return commonConfigProperties.getSupportedLocales()
            .stream()
            .map(
                language -> LanguageResponse.builder()
                    .language(language)
                    .actual(language.equals(currentLocale))
                    .build()
            )
            .collect(Collectors.toList());
    }

    public String getLanguage(UUID userId) {
        return userDao.findByIdValidated(userId).getLanguage();
    }
}
