package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.Power;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StarSystemPowerMappingFactoryTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @InjectMocks
    private StarSystemPowerMappingFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(STAR_SYSTEM_ID, Power.AISLING_DUVAL))
            .returns(STAR_SYSTEM_ID, StarSystemPowerMapping::getStarSystemId)
            .returns(Power.AISLING_DUVAL, StarSystemPowerMapping::getPower);
    }
}