package com.github.saphyra.apphub.service.skyxplore.facade;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SkyXplorePageController {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final SkyXploreLobbyApiClient lobbyClient;

    @GetMapping(Endpoints.SKYXPLORE_START_PAGE)
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
        mav.addObject("backUrl", Endpoints.SKYXPLORE_START_PAGE);
        return mav;
    }

    @GetMapping(Endpoints.SKYXPLORE_LOBBY_PAGE)
    public ModelAndView lobby(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        lobbyClient.createLobbyIfNotExists(accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated()); //TODO JavaScript should handle this
        ModelAndView mav = new ModelAndView("lobby");
        mav.addObject("userId", accessTokenHeader.getUserId());
        return mav;
    }

    @GetMapping(Endpoints.SKYXPLORE_JOIN_LOBBY_PAGE)
    public String joinLobby(@PathVariable("invitorId") UUID invitorId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        lobbyClient.joinLobby(
            invitorId,
            accessTokenHeaderConverter.convertDomain(accessTokenHeader),
            localeProvider.getLocaleValidated()
        );
        return "redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE; //TODO move lobby join to JavaScript, redirection does not work with ProductionProxy
    }
}
