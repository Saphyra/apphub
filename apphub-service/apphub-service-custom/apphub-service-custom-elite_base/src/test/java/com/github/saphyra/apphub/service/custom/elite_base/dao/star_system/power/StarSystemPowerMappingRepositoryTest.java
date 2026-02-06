package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class StarSystemPowerMappingRepositoryTest {
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";
    private static final String STAR_SYSTEM_ID_3 = "star-system-id-3";

    @Autowired
    private StarSystemPowerMappingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByStarSystemId() {
        StarSystemPowerMappingEntity entity1 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_1)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity1);
        StarSystemPowerMappingEntity entity2 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_2)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID_1)).containsExactly(entity1);
    }

    @Test
    void getByStarSystemIds() {
        StarSystemPowerMappingEntity entity1 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_1)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity1);
        StarSystemPowerMappingEntity entity2 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_2)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity2);
        StarSystemPowerMappingEntity entity3 = StarSystemPowerMappingEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_3)
            .power(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity3);

        assertThat(underTest.getByStarSystemIds(List.of(STAR_SYSTEM_ID_1, STAR_SYSTEM_ID_2))).containsExactlyInAnyOrder(entity1, entity2);
    }
}