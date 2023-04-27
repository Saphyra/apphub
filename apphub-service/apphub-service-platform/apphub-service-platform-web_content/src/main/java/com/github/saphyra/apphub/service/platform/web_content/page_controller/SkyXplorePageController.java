package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
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
    private final SkyXploreGameApiClient gameClient;

    @GetMapping(Endpoints.SKYXPLORE_GAME_PAGE)
    public ModelAndView game(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale) {
        if (!gameClient.isUserInGame(accessTokenHeaderConverter.convertDomain(accessTokenHeader), locale).getValue()) {
            return new ModelAndView("redirect:" + Endpoints.SKYXPLORE_LOBBY_PAGE);
        }
        return gameMav(accessTokenHeader.getUserId());
    }

    private ModelAndView gameMav(UUID userId) {
        ModelAndView mav = new ModelAndView("skyxplore/game");
        mav.addObject("userId", userId);
        return mav;
    }
}
