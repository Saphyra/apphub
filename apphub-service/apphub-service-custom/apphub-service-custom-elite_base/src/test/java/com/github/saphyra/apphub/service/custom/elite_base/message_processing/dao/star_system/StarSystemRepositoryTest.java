package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system;

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
class StarSystemRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final Long STAR_ID_1 = 34L;
    private static final String STAR_NAME = "star-name";

    @Autowired
    private StarSystemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarId() {
        StarSystemEntity entity = StarSystemEntity.builder()
            .id(ID_1)
            .starId(STAR_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarId(STAR_ID_1)).contains(entity);
    }

    @Test
    void findByStarName() {
        StarSystemEntity entity = StarSystemEntity.builder()
            .id(ID_1)
            .starName(STAR_NAME)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarName(STAR_NAME)).contains(entity);
    }
}