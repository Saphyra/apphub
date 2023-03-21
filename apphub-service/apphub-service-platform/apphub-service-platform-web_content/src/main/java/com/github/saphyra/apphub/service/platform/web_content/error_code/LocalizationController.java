package com.github.saphyra.apphub.service.platform.web_content.error_code;

import com.github.saphyra.apphub.api.platform.web_content.server.LocalizationApiController;
import com.github.saphyra.apphub.lib.common_domain.LocalizationKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LocalizationController implements LocalizationApiController {
    private final ErrorCodeService errorCodeService;
    private final TranslationService translationService;

    @Override
    public String translate(String errorCode, String locale) {
        log.debug("Translating errorCode {} to language {}", errorCode, locale);
        return errorCodeService.getByLocaleAndErrorCode(errorCode, locale);
    }

    @Override
    public String translateKey(LocalizationKey key, String locale) {
        log.debug("Translating key {} to language {}", key, locale);
        return translationService.translate(key, locale);
    }
}
