package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction;

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
class MinorFactionRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String FACTION_NAME_1 = "faction-name-1";

    @Autowired
    private MinorFactionRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByFactionName() {
        MinorFactionEntity entity = MinorFactionEntity.builder()
            .id(ID_1)
            .factionName(FACTION_NAME_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByFactionName(FACTION_NAME_1)).contains(entity);
    }
}