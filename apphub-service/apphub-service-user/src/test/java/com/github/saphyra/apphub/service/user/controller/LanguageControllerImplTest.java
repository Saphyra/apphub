package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LanguageControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private LanguageResponse languageResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void changeLanguage() {
        underTest.changeLanguage(accessTokenHeader, new OneParamRequest<>(LOCALE));

        verify(languageService).changeLanguage(USER_ID, LOCALE);
    }

    @Test
    public void getLanguage() {
        given(languageService.getLanguage(USER_ID)).willReturn(LOCALE);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void getLanguages() {
        given(languageService.getLanguages(USER_ID)).willReturn(Arrays.asList(languageResponse));

        List<LanguageResponse> result = underTest.getLanguages(accessTokenHeader);

        assertThat(result).containsExactly(languageResponse);
    }
}