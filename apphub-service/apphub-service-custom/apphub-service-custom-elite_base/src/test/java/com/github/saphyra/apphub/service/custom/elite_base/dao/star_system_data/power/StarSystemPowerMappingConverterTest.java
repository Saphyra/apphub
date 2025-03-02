package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemPowerMappingConverterTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StarSystemPowerMappingConverter underTest;

    @Test
    void convertDomain() {
        StarSystemPowerMapping domain = StarSystemPowerMapping.builder()
            .starSystemId(STAR_SYSTEM_ID)
            .power(Power.AISLING_DUVAL)
            .build();

        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(STAR_SYSTEM_ID_STRING, StarSystemPowerMappingEntity::getStarSystemId)
            .returns(Power.AISLING_DUVAL, StarSystemPowerMappingEntity::getPower);
    }

    @Test
    void convertEntity() {
        StarSystemPowerMappingEntity domain = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_STRING)
            .power(Power.AISLING_DUVAL)
            .build();

        given(uuidConverter.convertEntity(STAR_SYSTEM_ID_STRING)).willReturn(STAR_SYSTEM_ID);

        assertThat(underTest.convertEntity(domain))
            .returns(STAR_SYSTEM_ID, StarSystemPowerMapping::getStarSystemId)
            .returns(Power.AISLING_DUVAL, StarSystemPowerMapping::getPower);
    }
}