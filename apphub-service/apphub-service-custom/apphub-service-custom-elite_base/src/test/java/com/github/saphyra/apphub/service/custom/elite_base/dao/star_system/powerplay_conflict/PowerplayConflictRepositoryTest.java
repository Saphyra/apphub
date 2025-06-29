package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict.PowerplayConflictRepository;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class PowerplayConflictRepositoryTest {
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";

    @Autowired
    private PowerplayConflictRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByIdStarSystemId() {
        PowerplayConflictEntity entity1 = PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(STAR_SYSTEM_ID_1)
                .power(Power.NAKATO_KAINE)
                .build())
            .build();
        underTest.save(entity1);

        PowerplayConflictEntity entity2 = PowerplayConflictEntity.builder()
            .id(PowerplayConflictEntityId.builder()
                .starSystemId(STAR_SYSTEM_ID_2)
                .power(Power.NAKATO_KAINE)
                .build())
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByIdStarSystemId(STAR_SYSTEM_ID_1)).containsExactly(entity1);
    }
}