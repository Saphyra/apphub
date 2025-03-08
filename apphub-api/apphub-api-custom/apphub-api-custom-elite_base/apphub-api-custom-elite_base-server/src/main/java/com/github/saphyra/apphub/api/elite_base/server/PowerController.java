package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

//TODO role protection test
public interface PowerController {
    @GetMapping(EliteBaseEndpoints.ELITE_BASE_GET_POWERS)
    List<String> getPowers(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(EliteBaseEndpoints.ELITE_BASE_GET_POWERPLAY_STATES)
    List<String> getPowerplayStates(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
