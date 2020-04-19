package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class LocalizedMessageProvider {
    private final LocalizationApiClient localizationApi;
    private final MessageAssembler messageAssembler;

    String getLocalizedMessage(String locale, String errorCode, Map<String, String> params) {
        String messageBase = localizationApi.translate(errorCode, locale);
        return messageAssembler.assembleMessage(messageBase, params);
    }
}
