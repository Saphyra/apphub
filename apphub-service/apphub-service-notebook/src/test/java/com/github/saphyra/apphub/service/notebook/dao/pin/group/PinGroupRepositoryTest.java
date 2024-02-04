package com.github.saphyra.apphub.service.notebook.dao.pin.group;

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
class PinGroupRepositoryTest {
    private static final String PIN_GROUP_ID_1 = "pin-group-id-1";
    private static final String PIN_GROUP_ID_2 = "pin-group-id-";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private PinGroupRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        PinGroupEntity entity1 = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        PinGroupEntity entity2 = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        PinGroupEntity entity1 = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        PinGroupEntity entity2 = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);


        assertThat(underTest.getByUserId(USER_ID_2)).containsExactly(entity2);
    }
}