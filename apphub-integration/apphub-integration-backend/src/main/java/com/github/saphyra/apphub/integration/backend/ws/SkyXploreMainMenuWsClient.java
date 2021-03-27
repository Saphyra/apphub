package com.github.saphyra.apphub.integration.backend.ws;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
public class SkyXploreMainMenuWsClient extends AbstractWsClient {
    public SkyXploreMainMenuWsClient(Language language, UUID accessTokenId) throws URISyntaxException {
        super(language, Endpoints.CONNECTION_SKYXPLORE_MAIN_MENU, accessTokenId);
    }
}
