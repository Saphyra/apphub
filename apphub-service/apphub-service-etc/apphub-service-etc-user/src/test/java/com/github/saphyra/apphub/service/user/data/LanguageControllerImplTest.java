package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LanguageControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void changeLanguage() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.changeLanguage(accessTokenHeader, new OneParamRequest<>(LOCALE));

        verify(languageService).changeLanguage(USER_ID, LOCALE);
    }

    @Test
    public void getLanguage() {
        given(languageService.getLanguage(USER_ID)).willReturn(LOCALE);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LOCALE);
    }
}