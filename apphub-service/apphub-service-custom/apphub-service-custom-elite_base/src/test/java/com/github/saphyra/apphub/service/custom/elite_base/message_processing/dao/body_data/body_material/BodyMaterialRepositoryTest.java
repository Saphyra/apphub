package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material;

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
class BodyMaterialRepositoryTest {
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String BODY_ID_1 = "body-id-1";
    private static final String BODY_ID_2 = "body-id-2";

    @Autowired
    private BodyMaterialRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByBodyId() {
        BodyMaterialEntity entity1 = BodyMaterialEntity.builder()
            .id(ID_1)
            .bodyId(BODY_ID_1)
            .build();
        underTest.save(entity1);

        BodyMaterialEntity entity2 = BodyMaterialEntity.builder()
            .id(ID_2)
            .bodyId(BODY_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByBodyId(BODY_ID_1)).containsExactly(entity1);
    }
}