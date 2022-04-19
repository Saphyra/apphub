package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.api.user.client.AccountClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
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
    private final CharacterDao characterDao;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final SkyXploreGameApiClient gameClient;
    private final AccountClient userDataClient;

    @GetMapping(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)
    public ModelAndView mainMenu(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        log.info("Loading SkyXplore main menu for user {}", accessTokenHeader.getUserId());
        if (characterDao.exists(accessTokenHeader.getUserId())) {
            return new ModelAndView("main_menu");
        } else {
            log.info("User has no character. Redirecting to character page instead.");
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_CHARACTER_PAGE);
        }
    }

    @GetMapping(Endpoints.SKYXPLORE_CHARACTER_PAGE)
    public ModelAndView character(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        if (characterDao.exists(accessTokenHeader.getUserId())) {
            log.info("Loading SkyXplore character page.");
            ModelAndView mav = new ModelAndView("character");
            mav.addObject("backUrl", Endpoints.SKYXPLORE_MAIN_MENU_PAGE);
            mav.addObject("characterName", characterDao.findByIdValidated(accessTokenHeader.getUserId()).getName());
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
        mav.addObject("type", view.getType());
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
