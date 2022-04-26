package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.api.user.client.AccountClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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
    private CharacterDao characterDao;

    @Mock
    private SkyXploreLobbyApiClient lobbyClient;

    @Mock
    private SkyXploreGameApiClient gameClient;

    @Mock
    private AccountClient userDataClient;

    @InjectMocks
    private SkyXplorePageController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SkyXploreCharacter character;

    @Mock
    private LobbyViewForPage lobbyViewForPage;

    @Before
    public void setUp() {
        given(accessTokenHeaderConverter.convertDomain(accessTokenHeader)).willReturn(ACCESS_TOKEN_HEADER_STRING);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void mainMenu_characterDoesNotExist() {
        given(characterDao.exists(USER_ID)).willReturn(false);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_CHARACTER_PAGE);
    }

    @Test
    public void mainMenu() {
        given(characterDao.exists(USER_ID)).willReturn(true);

        ModelAndView result = underTest.mainMenu(accessTokenHeader);

        assertThat(result.getViewName()).isEqualTo("main_menu");
    }

    @Test
    public void character_notFound() {
        given(characterDao.exists(USER_ID)).willReturn(false);
        given(userDataClient.getUsernameByUserId(USER_ID, LOCALE)).willReturn(USERNAME);

        ModelAndView result = underTest.character(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.MODULES_PAGE);
        assertThat(result.getModel().get("characterName")).isEqualTo(USERNAME);
    }

    @Test
    public void character() {
        given(characterDao.exists(USER_ID)).willReturn(true);
        given(characterDao.findByIdValidated(USER_ID)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        ModelAndView result = underTest.character(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("character");
        assertThat(result.getModel().get("backUrl")).isEqualTo(Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
        assertThat(result.getModel().get("characterName")).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void lobby_userInGame() {
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(false);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(true);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_GAME_PAGE);
    }

    @Test
    public void lobby_userNotInLobby() {
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(false);
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(false);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
    }

    @Test
    public void lobby() {
        given(lobbyClient.lobbyForPage(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(lobbyViewForPage);
        given(lobbyViewForPage.isInLobby()).willReturn(true);
        given(lobbyViewForPage.getHost()).willReturn(HOST);
        given(lobbyViewForPage.isGameCreationStarted()).willReturn(true);
        given(lobbyViewForPage.getLobbyName()).willReturn(LOBBY_NAME);
        given(lobbyViewForPage.getType()).willReturn(TYPE);

        ModelAndView result = underTest.lobby(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("lobby");
        assertThat(result.getModel().get("userId")).isEqualTo(USER_ID);
        assertThat(result.getModel().get("host")).isEqualTo(HOST);
        assertThat(result.getModel().get("gameCreationStarted")).isEqualTo(true);
        assertThat(result.getModel().get("lobbyName")).isEqualTo(LOBBY_NAME);
        assertThat(result.getModel()).containsEntry("type", TYPE);
    }

    @Test
    public void game_notInGame() {
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(false);

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
    }

    @Test
    public void game() {
        given(gameClient.isUserInGame(ACCESS_TOKEN_HEADER_STRING, LOCALE)).willReturn(true);

        ModelAndView result = underTest.game(accessTokenHeader, LOCALE);

        assertThat(result.getViewName()).isEqualTo("game");
        assertThat(result.getModel().get("userId")).isEqualTo(USER_ID);
    }
}