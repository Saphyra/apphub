package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

public interface StarSystemController {
    @PostMapping(EliteBaseEndpoints.ELITE_BASE_STAR_SYSTEMS_SEARCH)
    Map<UUID, String> search(@RequestBody OneParamRequest<String> query, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
