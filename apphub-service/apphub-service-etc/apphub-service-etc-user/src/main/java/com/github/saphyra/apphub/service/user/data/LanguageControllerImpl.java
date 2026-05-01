package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.etc.user.server.LanguageController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class LanguageControllerImpl implements LanguageController {
    private final LanguageService languageService;

    @Override
    public void changeLanguage(AccessToken accessToken, OneParamRequest<String> language) {
        log.info("{} wants to change his language", accessToken.getUserId());
        log.debug("Request payload: {}", language);
        languageService.changeLanguage(accessToken.getUserId(), language.getValue());
    }

    @Override
    public String getLanguageInternal(UUID userId) {
        return languageService.getLanguage(userId);
    }
}
