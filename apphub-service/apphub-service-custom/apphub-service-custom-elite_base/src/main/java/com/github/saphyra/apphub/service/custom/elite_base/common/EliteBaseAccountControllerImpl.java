package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.api.elite_base.server.EliteBaseAccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class EliteBaseAccountControllerImpl implements EliteBaseAccountController {
    @Override
    public boolean isAdmin(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know if they are an EliteBase Admin", accessTokenHeader.getUserId());

        return accessTokenHeader.getRoles()
            .contains(EliteBaseConstants.ROLE_ELITE_BASE_ADMIN);
    }
}
