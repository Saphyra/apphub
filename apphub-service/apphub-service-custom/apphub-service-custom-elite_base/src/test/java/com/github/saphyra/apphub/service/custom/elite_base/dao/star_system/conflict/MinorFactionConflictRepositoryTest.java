package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflictEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.MinorFactionConflictRepository;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class MinorFactionConflictRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String STAR_SYSTEM_ID_1 = "star-system-id-1";
    private static final String STAR_SYSTEM_ID_2 = "star-system-id-2";

    @Autowired
    private MinorFactionConflictRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByStarSystemId() {
        MinorFactionConflictEntity entity1 = MinorFactionConflictEntity.builder()
            .id(ID_1)
            .starSystemId(STAR_SYSTEM_ID_1)
            .build();
        underTest.save(entity1);
        MinorFactionConflictEntity entity2 = MinorFactionConflictEntity.builder()
            .id(ID_2)
            .starSystemId(STAR_SYSTEM_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByStarSystemId(STAR_SYSTEM_ID_1)).containsExactly(entity1);
    }
}