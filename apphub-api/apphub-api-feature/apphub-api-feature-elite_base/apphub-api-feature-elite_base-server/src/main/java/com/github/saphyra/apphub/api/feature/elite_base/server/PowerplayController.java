package com.github.saphyra.apphub.api.feature.elite_base.server;

import com.github.saphyra.apphub.api.feature.elite_base.model.EliteBaseEndpoints;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface PowerplayController {
    //TODO role protection test
    @GetMapping(EliteBaseEndpoints.ELITE_BASE_MERIT_FARM_GET_MINE_LOCATIONS)
    List<MiningMeritFarmResponse> meritFarmGetMiningLocations(@PathVariable("power") String power, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
