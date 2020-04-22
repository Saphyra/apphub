package com.github.saphyra.apphub.service.platform.localization;

import com.github.saphyra.apphub.api.platform.localization.server.LocalizationApiController;
import com.github.saphyra.apphub.service.platform.localization.error_code.ErrorCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LocalizationController implements LocalizationApiController {
    private final ErrorCodeService errorCodeService;

    @Override
    public String translate(String errorCode, String locale) {
        log.info("Translating errorCode {} to language {}", errorCode, locale);
        return errorCodeService.getByLocaleAndErrorCode(errorCode, locale);
    }
}
