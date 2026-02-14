package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.NearestMaterialTraderResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface NearestController {
    @GetMapping(EliteBaseEndpoints.ELITE_BASE_NEAREST_MATERIAL_TRADERS)
    List<NearestMaterialTraderResponse> getNearestMaterialTraders(
        @PathVariable("starId") UUID starId,
        @PathVariable("materialType") MaterialType materialType,
        @PathVariable("page") Integer page,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );
}
