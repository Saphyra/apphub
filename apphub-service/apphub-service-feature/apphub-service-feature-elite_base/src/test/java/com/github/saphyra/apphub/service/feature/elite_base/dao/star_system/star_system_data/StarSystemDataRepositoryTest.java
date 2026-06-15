package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data;

import com.github.saphyra.apphub.test.repository.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class StarSystemDataRepositoryTest {
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";

    @Autowired
    private StarSystemDataRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByControllingPower() {
        StarSystemDataEntity entity1 = StarSystemDataEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_1)
            .controllingPower(Power.NAKATO_KAINE)
            .build();
        underTest.save(entity1);

        StarSystemDataEntity entity2 = StarSystemDataEntity.builder()
            .starSystemId(STAR_SYSTEM_ID_2)
            .controllingPower(Power.AISLING_DUVAL)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByControllingPower(Power.NAKATO_KAINE)).containsExactlyInAnyOrder(entity1);
    }
}