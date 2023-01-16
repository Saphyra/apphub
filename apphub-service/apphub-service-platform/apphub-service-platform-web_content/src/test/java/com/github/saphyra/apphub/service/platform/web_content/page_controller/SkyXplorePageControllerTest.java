package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.api.user.client.AccountClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private static final String USERNAME = "username";
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID HOST = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final String TYPE = "type";

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private SkyXploreCharacterDataApiClient characterClient;

    @Mock
    private SkyXploreLobbyApiClient lobbyClient;

    @Mock
    private SkyXploreGameApiClient gameClient;

    @Mock
    private AccountClient userDataClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private SkyXplorePageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private LobbyViewForPage lobbyViewForPage;

    @Mock
    private SkyXploreCharacterModel character;

    @Test
    public void mainMenu_characterDoesNotExist() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(characterClient.exists(USER_ID, LOCALE)).willReturn(false);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_CHARACTER_PAGE);
    }

    @Test
    public void mainMenu() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(characterClient.exists(USER_ID, LOCALE)).willReturn(true);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("skyxplore/main_menu");
    }

    @Test
    public void character_notFound() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(characterClient.exists(USER_ID, LOCALE)).willReturn(false);
        given(userDataClient.getUsernameByUserId(USER_ID, LOCALE)).willReturn(USERNAME);

        ModelAndView result = underTest.character(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("skyxplore/character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.MODULES_PAGE);
        assertThat(result.getModel().get("characterName")).isEqualTo(USERNAME);
    }

    @Test
    public void character() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(characterClient.exists(USER_ID, LOCALE)).willReturn(true);
        given(characterClient.internalGetCharacterByUserId(USER_ID, LOCALE)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        ModelAndView result = underTest.character(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("skyxplore/character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
        assertThat(result.getModel().get("characterName")).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void lobby_userInGame() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(false);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(true);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_GAME_PAGE);
    }

    @Test
    public void lobby_userNotInLobby() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(false);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(false);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
    }

    @Test
    public void lobby() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(true);
        given(lobbyViewForPage.getHost()).willReturn(HOST);
        given(lobbyViewForPage.isGameCreationStarted()).willReturn(true);
        given(lobbyViewForPage.getLobbyName()).willReturn(LOBBY_NAME);
        given(lobbyViewForPage.getType()).willReturn(TYPE);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("skyxplore/lobby");
        assertThat(result.getModel().get("userId")).isEqualTo(USER_ID);
        assertThat(result.getModel().get("host")).isEqualTo(HOST);
        assertThat(result.getModel().get("gameCreationStarted")).isEqualTo(true);
        assertThat(result.getModel().get("lobbyName")).isEqualTo(LOBBY_NAME);
        assertThat(result.getModel()).containsEntry("type", TYPE);
    }

    @Test
    public void game_notInGame() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(false);

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
    }

    @Test
    public void game() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(true);

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("skyxplore/game");
        assertThat(result.getModel().get("userId")).isEqualTo(USER_ID);
    }
}