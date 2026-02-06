package com.github.saphyra.apphub.service.custom.elite_base.service.nearest;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.NearestMaterialTraderResponse;
import com.github.saphyra.apphub.api.elite_base.server.NearestController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.elite_base.service.nearest.material_trader.NearestMaterialTraderService;
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
    public List<NearestMaterialTraderResponse> getNearestMaterialTraders(UUID starId, MaterialType materialType, Integer page, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the page {} of the nearest {} material traders to star {}", accessTokenHeader.getUserId(), page, materialType, starId);

        return nearestMaterialTraderService.getNearestMaterialTraders(starId, materialType, page);
    }
}
