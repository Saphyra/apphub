package com.github.saphyra.apphub.service.platform.localization;

import com.github.saphyra.apphub.api.platform.localization.server.LocalizationApi;
import com.github.saphyra.apphub.service.platform.localization.error_code.ErrorCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//TODO unit test
//TODO int test
//TODO api test
public class LocalizationController implements LocalizationApi {
    private final ErrorCodeService errorCodeService;

    @Override
    public String translate(String errorCode, String locale) {
        return errorCodeService.getByLocaleAndErrorCode(errorCode, locale);
    }
}
