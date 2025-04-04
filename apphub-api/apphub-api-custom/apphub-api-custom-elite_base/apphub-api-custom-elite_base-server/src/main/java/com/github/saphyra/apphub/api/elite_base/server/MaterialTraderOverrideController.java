package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.api.custom.elite_base.model.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface MaterialTraderOverrideController {
    @PutMapping(EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_CREATE)
    void createOverride(@RequestBody CreateMaterialTraderOverrideRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_DELETE)
    void deleteOverride(@PathVariable("stationId") UUID stationId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(EliteBaseEndpoints.ELITE_BASE_MATERIAL_TRADER_OVERRIDE_VERIFY)
    void verifyOverride(@PathVariable("stationId") UUID stationId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
