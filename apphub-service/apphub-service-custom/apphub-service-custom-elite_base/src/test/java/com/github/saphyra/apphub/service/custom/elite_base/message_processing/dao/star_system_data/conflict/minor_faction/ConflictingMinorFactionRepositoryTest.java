package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
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
class ConflictingMinorFactionRepositoryTest {
    private static final String CONFLICT_ID_1 = "conflict-id-1";
    private static final String CONFLICT_ID_2 = "conflict-id-2";
    private static final String FACTION_ID_1 = "faction-id-1";
    private static final String FACTION_ID_2 = "faction-id-2";

    @Autowired
    private ConflictingMinorFactionRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByConflictId() {
        ConflictingMinorFactionEntity entity1 = ConflictingMinorFactionEntity.builder()
            .conflictId(CONFLICT_ID_1)
            .factionId(FACTION_ID_1)
            .build();
        underTest.save(entity1);
        ConflictingMinorFactionEntity entity2 = ConflictingMinorFactionEntity.builder()
            .conflictId(CONFLICT_ID_2)
            .factionId(FACTION_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByConflictId(CONFLICT_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByConflictId() {
        ConflictingMinorFactionEntity entity1 = ConflictingMinorFactionEntity.builder()
            .conflictId(CONFLICT_ID_1)
            .factionId(FACTION_ID_1)
            .build();
        underTest.save(entity1);
        ConflictingMinorFactionEntity entity2 = ConflictingMinorFactionEntity.builder()
            .conflictId(CONFLICT_ID_2)
            .factionId(FACTION_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByConflictId(CONFLICT_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}