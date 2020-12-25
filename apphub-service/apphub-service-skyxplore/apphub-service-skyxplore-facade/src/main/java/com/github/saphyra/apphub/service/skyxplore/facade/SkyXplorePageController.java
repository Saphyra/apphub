package com.github.saphyra.apphub.service.skyxplore.facade;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkyXplorePageController {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final SkyXploreGameApiClient gameClient;

    @GetMapping(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)
    public ModelAndView mainMenu(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        log.info("Loading SkyXplore main menu for user {}", accessTokenHeader.getUserId());
        if (characterClient.isCharacterExistsForUser(accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated())) {
            return new ModelAndView("main_menu");
        } else {
            log.info("User has no character. Returning character page instead.");
            ModelAndView mav = new ModelAndView("character");
            mav.addObject("backUrl", Endpoints.MODULES_PAGE);
            return mav;
        }
    }

    @GetMapping(Endpoints.SKYXPLORE_CHARACTER_PAGE)
    public ModelAndView character() {
        log.info("Loading SkyXplore character page.");
        ModelAndView mav = new ModelAndView("character");
        mav.addObject("backUrl", Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
        return mav;
    }

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_PAGE)
    public ModelAndView lobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        LobbyViewForPage view = lobbyClient.lobbyForPage(accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated());
        if (!view.isInLobby()) {
            log.info("User is not in lobby.");
            //TODO check if player is in game
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
        }
        ModelAndView mav = new ModelAndView("lobby");
        mav.addObject("userId", accessTokenHeader.getUserId());
        mav.addObject("host", view.getHost());
        mav.addObject("gameCreationStarted", view.isGameCreationStarted());
        return mav;
    }

    @GetMapping(Endpoints.SKYXPLORE_GAME_PAGE)
    public ModelAndView game(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        if (!gameClient.isUserInGame(accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated())) {
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
        }
        ModelAndView mav = new ModelAndView("game");
        mav.addObject("userId", accessTokenHeader.getUserId());
        return mav;
    }
}
