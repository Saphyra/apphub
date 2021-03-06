package com.github.saphyra.apphub.lib.error_handler.service;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LocalizedMessageProviderTest {
    private static final String LOCALE = "locale";
    private static final String ERROR_CODE = "error-code";
    private static final String MESSAGE_BASE = "message-base";
    private static final String LOCALIZED_MESSAGE = "localized-message";

    @Mock
    private LocalizationApiClient localizationApi;

    @Mock
    private MessageAssembler messageAssembler;

    @InjectMocks
    private LocalizedMessageProvider underTest;

    @Mock
    private Map<String, String> params;

    @Test
    public void getLocalizedMessage() {
        given(localizationApi.translate(ERROR_CODE, LOCALE)).willReturn(MESSAGE_BASE);
        given(messageAssembler.assembleMessage(MESSAGE_BASE, params)).willReturn(LOCALIZED_MESSAGE);

        String result = underTest.getLocalizedMessage(LOCALE, ERROR_CODE, params);

        assertThat(result).isEqualTo(LOCALIZED_MESSAGE);
    }
}