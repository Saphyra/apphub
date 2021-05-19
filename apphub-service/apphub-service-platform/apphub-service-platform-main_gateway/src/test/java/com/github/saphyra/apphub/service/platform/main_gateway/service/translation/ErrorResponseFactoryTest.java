package com.github.saphyra.apphub.service.platform.main_gateway.service.translation;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorResponseFactoryTest {
    private static final String LOCALE = "locale";
    private static final String ERROR_CODE = "error-code";
    private static final String LOCALIZED_MESSAGE = "localized-message";

    @Mock
    private LocalizedMessageProvider localizedMessageProvider;

    @InjectMocks
    private ErrorResponseFactory underTest;

    @Test
    public void create() {
        given(localizedMessageProvider.getLocalizedMessage(LOCALE, ERROR_CODE, new HashMap<>())).willReturn(LOCALIZED_MESSAGE);

        ErrorResponseWrapper result = underTest.create(LOCALE, HttpStatus.BAD_REQUEST, ERROR_CODE, new HashMap<>());

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getErrorResponse().getErrorCode()).isEqualTo(ERROR_CODE);
        assertThat(result.getErrorResponse().getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(result.getErrorResponse().getParams()).isNotNull();
    }
}