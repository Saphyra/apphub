package com.github.saphyra.apphub.service.custom.elite_base.service.power;

import com.github.saphyra.apphub.api.elite_base.server.PowerController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
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
    public List<String> getPowers(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the powers.", accessTokenHeader.getUserId());

        return Arrays.stream(Power.values())
            .map(Enum::name)
            .toList();
    }

    @Override
    public List<String> getPowerplayStates(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the powerplay states.", accessTokenHeader.getUserId());

        return Arrays.stream(PowerplayState.values())
            .map(Enum::name)
            .toList();
    }
}
