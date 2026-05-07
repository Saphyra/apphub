package com.github.saphyra.apphub.api.feature.elite_base.server;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.feature.elite_base.model.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface EliteBaseAccountController {
    @GetMapping(EliteBaseEndpoints.ELITE_BASE_IS_ADMIN)
    boolean isAdmin(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
