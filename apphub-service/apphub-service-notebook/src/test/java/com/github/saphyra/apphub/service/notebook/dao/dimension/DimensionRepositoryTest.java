package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class DimensionRepositoryTest {
    private static final String DIMENSION_ID_1 = "dimension-id-1";
    private static final String DIMENSION_ID_2 = "dimension-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";

    @Autowired
    private DimensionRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        DimensionEntity entity1 = DimensionEntity.builder()
            .dimensionId(DIMENSION_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        DimensionEntity entity2 = DimensionEntity.builder()
            .dimensionId(DIMENSION_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByExternalReference() {
        DimensionEntity entity1 = DimensionEntity.builder()
            .dimensionId(DIMENSION_ID_1)
            .externalReference(EXTERNAL_REFERENCE_1)
            .build();
        underTest.save(entity1);

        DimensionEntity entity2 = DimensionEntity.builder()
            .dimensionId(DIMENSION_ID_2)
            .externalReference(EXTERNAL_REFERENCE_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByExternalReference(EXTERNAL_REFERENCE_1)).containsExactly(entity1);
    }
}