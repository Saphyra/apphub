package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocalizedMessageProvider {
    private final LocalizationApiClient localizationApi;
    private final MessageAssembler messageAssembler;

    public String getLocalizedMessage(String locale, ErrorCode errorCode) {
        return getLocalizedMessage(locale, errorCode, new HashMap<>());
    }

    public String getLocalizedMessage(String locale, ErrorCode errorCode, Map<String, String> params) {
        String messageBase = localizationApi.translate(errorCode.name(), locale);
        return messageAssembler.assembleMessage(messageBase, params);
    }
}
