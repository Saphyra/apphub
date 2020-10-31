package com.github.saphyra.apphub.service.skyxplore.lobby;

import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class
})
@EnableLocalMandatoryRequestValidation
public class SkyxploreDataLobbyConfiguration {
}
