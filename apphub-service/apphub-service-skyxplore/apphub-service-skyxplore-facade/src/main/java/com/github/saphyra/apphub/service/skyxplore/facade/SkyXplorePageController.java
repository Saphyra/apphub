package com.github.saphyra.apphub.service.skyxplore.facade;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterApiClient;
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

@Controller
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkyXplorePageController {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterApiClient characterClient;

    @GetMapping(Endpoints.SKYXPLORE_START_PAGE)
    public String mainMenu(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) {
        log.info("Loading SkyXplore main menu for user {}", accessTokenHeader.getUserId());
        if (characterClient.isCharacterExistsForUser(accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated())) {
            return "main_menu";
        } else {
            return "character";
        }
    }
}
