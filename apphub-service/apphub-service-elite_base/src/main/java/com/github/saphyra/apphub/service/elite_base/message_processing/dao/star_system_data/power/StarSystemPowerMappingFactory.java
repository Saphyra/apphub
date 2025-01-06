package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.Power;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemPowerMappingFactory {
    public StarSystemPowerMapping create(UUID starSystemId, Power power) {
        return StarSystemPowerMapping.builder()
            .starSystemId(starSystemId)
            .power(power)
            .build();
    }
}
