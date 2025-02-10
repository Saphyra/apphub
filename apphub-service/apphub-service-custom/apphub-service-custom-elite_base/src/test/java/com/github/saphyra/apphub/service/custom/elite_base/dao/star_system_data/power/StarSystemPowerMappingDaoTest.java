package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.power.StarSystemPowerMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemPowerMappingDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StarSystemPowerMappingConverter converter;

    @Mock
    private StarSystemPowerMappingRepository repository;

    @InjectMocks
    private StarSystemPowerMappingDao underTest;

    @Mock
    private StarSystemPowerMapping domain;

    @Mock
    private StarSystemPowerMappingEntity entity;

    @Test
    void getByStarSystemId() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.getByStarSystemId(STAR_SYSTEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID)).containsExactly(domain);
    }
}