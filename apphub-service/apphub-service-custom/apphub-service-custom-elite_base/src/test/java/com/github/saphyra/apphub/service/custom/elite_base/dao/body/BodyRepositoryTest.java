package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyEntity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyRepository;
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
class BodyRepositoryTest {
    private static final String ID = "id";
    private static final String STAR_SYSTEM_ID = "star-system-id";
    private static final Long BODY_ID = 34L;
    private static final String BODY_NAME = "body-name";

    @Autowired
    private BodyRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void findByStarSystemIdAndBodyId() {
        BodyEntity entity = BodyEntity.builder()
            .id(ID)
            .starSystemId(STAR_SYSTEM_ID)
            .bodyId(BODY_ID)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByStarSystemIdAndBodyId(STAR_SYSTEM_ID, BODY_ID)).contains(entity);
    }

    @Test
    void findByBodyName() {
        BodyEntity entity = BodyEntity.builder()
            .id(ID)
            .bodyName(BODY_NAME)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByBodyName(BODY_NAME)).contains(entity);
    }
}