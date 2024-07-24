package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameProxyTest {
    private static final String ACCESS_TOKEN = "access-token";
    private static final String LOCALE = "locale";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreGameApiClient gameClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private GameProxy underTest;

    @BeforeEach
    void setUp() {
        given(accessTokenProvider.getAsString()).willReturn(ACCESS_TOKEN);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    void getGameIdValidated_notFound() {
        given(gameClient.getGameId(ACCESS_TOKEN, LOCALE)).willReturn(new OneParamResponse<>());

        ExceptionValidator.validateForbiddenOperation(catchThrowable(() -> underTest.getGameIdValidated(USER_ID)));
    }

    @Test
    void getGameIdValidated() {
        given(gameClient.getGameId(ACCESS_TOKEN, LOCALE)).willReturn(new OneParamResponse<>(GAME_ID));

        assertThat(underTest.getGameIdValidated(USER_ID)).isEqualTo(GAME_ID);
    }
}