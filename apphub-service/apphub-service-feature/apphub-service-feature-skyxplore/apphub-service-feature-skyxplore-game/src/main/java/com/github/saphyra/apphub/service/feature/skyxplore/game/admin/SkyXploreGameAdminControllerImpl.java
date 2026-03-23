package com.github.saphyra.apphub.service.feature.skyxplore.game.admin;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.game.server.platform.SkyXploreGameAdminController;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.skyxplore.game.admin.service.AdminQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class SkyXploreGameAdminControllerImpl implements SkyXploreGameAdminController {
    private final Map<GameItemType, AdminQueryService> services;

    public SkyXploreGameAdminControllerImpl(List<AdminQueryService> services) {
        this.services = services.stream()
            .collect(Collectors.toMap(AdminQueryService::getType, adminQueryService -> adminQueryService));
    }

    @Override
    public List<SkyXploreGameDataEntry> getByType(GameItemType type, UUID gameId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query {}s of game {}", accessTokenHeader.getUserId(), type, gameId);

        return getService(type)
            .getAll(gameId);
    }

    @Override
    public SkyXploreGameDataDetails getItem(UUID gameId, GameItemType type, UUID itemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query {} - {} of game {}", accessTokenHeader.getUserId(), type, itemId, gameId);

        return getService(type)
            .findById(gameId, itemId);
    }

    private AdminQueryService getService(GameItemType type) {
        return Optional.ofNullable(services.get(type))
            .orElseThrow(() -> ExceptionFactory.invalidParam("type", "not implemented"));
    }
}
