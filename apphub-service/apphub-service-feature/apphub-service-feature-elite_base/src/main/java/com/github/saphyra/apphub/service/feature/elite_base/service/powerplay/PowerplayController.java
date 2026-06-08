package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay;

import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.server.PowerplayController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine.NakatoKaineMeritMinerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PowerplayControllerImp implements PowerplayController {
    private final NakatoKaineMeritMinerService nakatoKaineMeritMinerService;

    @Override
    public List<MiningMeritFarmResponse> meritFarmGetMiningLocations(String powerString, AccessToken accessToken) {
        Power power = ValidationUtil.parse(powerString, _ -> Power.valueOf(powerString), "power");

        log.info("Wants to know the merit-mine locations for Power {}", power);

        return switch (power) {
            case NAKATO_KAINE -> nakatoKaineMeritMinerService.getLocations();
            default -> throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TYPE, "Merit-mining is not available for power " + power);
        };
    }
}
