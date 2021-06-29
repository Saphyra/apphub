package com.github.saphyra.apphub.service.skyxplore.facade;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.api.user.client.UserDataApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SkyXplorePageController {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final SkyXploreGameApiClient gameClient;
    private final UserDataApiClient userDataClient;

    @GetMapping(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)
    public ModelAndView mainMenu(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        log.info("Loading SkyXplore main menu for user {}", accessTokenHeader.getUserId());
        if (characterClient.doesCharacterExistForUser(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale)) {
            return new ModelAndView("main_menu");
        } else {
            log.info("User has no character. Redirecting to character page instead.");
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_CHARACTER_PAGE);
        }
    }

    @GetMapping(Endpoints.SKYXPLORE_CHARACTER_PAGE)
    public ModelAndView character(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        if (characterClient.doesCharacterExistForUser(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale)) {
            log.info("Loading SkyXplore character page.");
            ModelAndView mav = new ModelAndView("character");
            mav.addObject("backUrl", Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
            mav.addObject("characterName", characterClient.internalGetCharacterByUserId(accessTokenHeader.getUserId(), locale).getName());
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("character");
            mav.addObject("backUrl", Endpoints.MODULES_PAGE);
            mav.addObject("characterName", userDataClient.getUsernameByUserId(accessTokenHeader.getUserId(), locale));
            return mav;
        }
    }

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_PAGE)
    public ModelAndView lobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        LobbyViewForPage view = lobbyClient.lobbyForPage(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale);
        if (!view.isInLobby()) {
            log.info("User is not in lobby.");
            if (gameClient.isUserInGame(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale)) {
                return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_GAME_PAGE);
            }
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
        }
        ModelAndView mav = new ModelAndView("lobby");
        mav.addObject("userId", accessTokenHeader.getUserId());
        mav.addObject("host", view.getHost());
        mav.addObject("gameCreationStarted", view.isGameCreationStarted());
        mav.addObject("lobbyName", view.getLobbyName());
        return mav;
    }

    @GetMapping(Endpoints.SKYXPLORE_GAME_PAGE)
    public ModelAndView game(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        if (!gameClient.isUserInGame(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale)) {
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
        }
        return gameMav(accessTokenHeader.getUserId());
    }

    private ModelAndView gameMav(UUID userId) {
        ModelAndView mav = new ModelAndView("game");
        mav.addObject("userId", userId);
        return mav;
    }
}
