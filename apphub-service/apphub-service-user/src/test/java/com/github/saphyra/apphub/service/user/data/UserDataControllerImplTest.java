package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserDataControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private LanguageService languageService;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private UserDataControllerImpl underTest;

    @Mock
    private RegistrationRequest registrationRequest;

    @Test
    public void getLanguage() {
        given(languageService.getLanguage(USER_ID)).willReturn(LOCALE);

        String result = underTest.getLanguage(USER_ID);

        assertThat(result).isEqualTo(LOCALE);
    }

    @Test
    public void register() {
        underTest.register(registrationRequest, LOCALE);

        verify(registrationService).register(registrationRequest, LOCALE);
    }
}