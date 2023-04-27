package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkyXplorePageControllerTest {
    private static final String LOCALE = "locale";
    private static final String ACCESS_TOKEN_HEADER_STRING = "access-token-header";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private SkyXploreGameApiClient gameClient;

    @InjectMocks
    private SkyXplorePageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void game_notInGame() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(new OneParamResponse<>(false));

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
    }

    @Test
    public void game() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(new OneParamResponse<>(true));

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("skyxplore/game");
        assertThat(result.getModel().get("userId")).isEqualTo(USER_ID);
    }
}