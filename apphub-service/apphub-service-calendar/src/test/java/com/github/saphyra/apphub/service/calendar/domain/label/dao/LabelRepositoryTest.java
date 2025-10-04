package com.github.saphyra.apphub.service.calendar.domain.label.dao;

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
class LabelRepositoryTest {
    private static final String LABEL_ID_1 = "label-id-1";
    private static final String LABEL_ID_2 = "label-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private LabelRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        LabelEntity entity1 = LabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        LabelEntity entity2 = LabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        LabelEntity entity1 = LabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        LabelEntity entity2 = LabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndLabelId() {
        LabelEntity entity1 = LabelEntity.builder()
            .labelId(LABEL_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        LabelEntity entity2 = LabelEntity.builder()
            .labelId(LABEL_ID_2)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndLabelId(USER_ID_1, LABEL_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}