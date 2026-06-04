package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import com.github.saphyra.apphub.test.repository.RepositoryTestConfiguration;
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
@Deprecated(forRemoval = true)
class DeprecatedLabelRepositoryTest {
    private static final String LABEL_ID_1 = "label-id-1";
    private static final String LABEL_ID_2 = "label-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private DeprecatedLabelRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        DeprecatedLabelEntity entity1 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedLabelEntity entity2 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        DeprecatedLabelEntity entity1 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedLabelEntity entity2 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndLabelId() {
        DeprecatedLabelEntity entity1 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        DeprecatedLabelEntity entity2 = DeprecatedLabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndLabelId(USER_ID_1, LABEL_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}