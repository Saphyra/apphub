package com.github.saphyra.apphub.service.feature.elite_base.common;

import com.github.saphyra.apphub.api.feature.elite_base.server.EliteBaseAccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class EliteBaseAccountControllerImpl implements EliteBaseAccountController {
    @Override
    public boolean isAdmin(AccessToken accessToken) {
        log.info("{} wants to know if they are an EliteBase Admin", accessToken.getUserId());

        return accessToken.getRoles()
            .contains(EliteBaseConstants.ROLE_ELITE_BASE_ADMIN);
    }
}
