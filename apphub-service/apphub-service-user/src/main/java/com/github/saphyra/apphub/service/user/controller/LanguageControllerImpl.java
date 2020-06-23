package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.api.user.server.LanguageController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LanguageControllerImpl implements LanguageController {
    private final LanguageService languageService;

    @Override
    public void changeLanguage(AccessTokenHeader accessTokenHeader, OneParamRequest<String> language) {
        log.info("{} wants to change his language", accessTokenHeader.getUserId());
        log.debug("Request payload: {}", language);
        languageService.changeLanguage(accessTokenHeader.getUserId(), language.getValue());
    }

    @Override
    public List<LanguageResponse> getLanguages(AccessTokenHeader accessTokenHeader) {
        log.info("Querying available languages for user {}", accessTokenHeader.getUserId());
        return languageService.getLanguages(accessTokenHeader.getUserId());
    }

    @Override
    public String getLanguage(UUID userId) {
        return languageService.getLanguage(userId);
    }
}
