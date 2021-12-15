package com.github.saphyra.apphub.lib.error_handler.service.translation;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationClient;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
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
    private static final String MESSAGE_BASE = "message-base";
    private static final String LOCALIZED_MESSAGE = "localized-message";

    @Mock
    private LocalizationClient localizationApi;

    @Mock
    private MessageAssembler messageAssembler;

    @InjectMocks
    private LocalizedMessageProvider underTest;

    @Mock
    private Map<String, String> params;

    @Test
    public void getLocalizedMessage() {
        given(localizationApi.translate(ErrorCode.GENERAL_ERROR.name(), LOCALE)).willReturn(MESSAGE_BASE);
        given(messageAssembler.assembleMessage(MESSAGE_BASE, params)).willReturn(LOCALIZED_MESSAGE);

        String result = underTest.getLocalizedMessage(LOCALE, ErrorCode.GENERAL_ERROR, params);

        assertThat(result).isEqualTo(LOCALIZED_MESSAGE);
    }
}