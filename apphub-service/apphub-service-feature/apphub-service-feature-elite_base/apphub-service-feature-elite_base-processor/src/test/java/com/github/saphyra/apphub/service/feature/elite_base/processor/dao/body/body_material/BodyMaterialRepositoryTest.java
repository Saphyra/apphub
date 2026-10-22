package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import com.github.saphyra.apphub.lib.test.repository.RepositoryTestConfiguration;
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
    private static final String BODY_ID_1 = "body-id-1";
    private static final String BODY_ID_2 = "body-id-2";
    private static final String MATERIAL = "material";

    @Autowired
    private BodyMaterialRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    void getByBodyId() {
        BodyMaterialEntity entity1 = BodyMaterialEntity.builder()
            .id(new BodyMaterialEntityId(BODY_ID_1, MATERIAL))
            .build();
        underTest.save(entity1);

        BodyMaterialEntity entity2 = BodyMaterialEntity.builder()
            .id(new BodyMaterialEntityId(BODY_ID_2, MATERIAL))
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByBodyId(BODY_ID_1)).containsExactly(entity1);
    }
}