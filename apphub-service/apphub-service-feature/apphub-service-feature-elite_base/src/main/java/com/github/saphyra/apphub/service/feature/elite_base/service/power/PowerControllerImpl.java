package com.github.saphyra.apphub.service.feature.elite_base.service.power;

import com.github.saphyra.apphub.api.feature.elite_base.server.PowerController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PowerControllerImpl implements PowerController {
    @Override
    public List<String> getPowers(AccessToken accessToken) {
        log.info("{} wants to know the powers.", accessToken.getUserId());

        return Arrays.stream(Power.values())
            .map(Enum::name)
            .toList();
    }

    @Override
    public List<String> getPowerplayStates(AccessToken accessToken) {
        log.info("{} wants to know the powerplay states.", accessToken.getUserId());

        return Arrays.stream(PowerplayState.values())
            .map(Enum::name)
            .toList();
    }
}
