package com.github.saphyra.apphub.service.feature.elite_base.service.nearest;

import com.github.saphyra.apphub.api.feature.elite_base.model.MaterialType;
import com.github.saphyra.apphub.api.feature.elite_base.model.material_trader.NearestMaterialTraderResponse;
import com.github.saphyra.apphub.api.feature.elite_base.server.NearestController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.elite_base.service.nearest.material_trader.NearestMaterialTraderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
class NearestControllerImpl implements NearestController {
    private final NearestMaterialTraderService nearestMaterialTraderService;

    @Override
    public List<NearestMaterialTraderResponse> getNearestMaterialTraders(UUID starId, MaterialType materialType, Integer page, AccessToken accessToken) {
        log.info("{} wants to know the page {} of the nearest {} material traders to star {}", accessToken.getUserId(), page, materialType, starId);

        return nearestMaterialTraderService.getNearestMaterialTraders(starId, materialType, page);
    }
}
