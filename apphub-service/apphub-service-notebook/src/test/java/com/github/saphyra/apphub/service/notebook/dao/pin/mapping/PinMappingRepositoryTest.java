package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

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
class PinMappingRepositoryTest {
    private static final String PIN_MAPPING_ID_1 = "pin-mapping-id-1";
    private static final String PIN_MAPPING_ID_2 = "pin-mapping-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String PIN_GROUP_ID_1 = "pin-group-id-1";
    private static final String PIN_GROUP_ID_2 = "pin-group-id-2";
    private static final String LIST_ITEM_ID_1 = "list-item-id-1";
    private static final String LIST_ITEM_ID_2 = "list-item-id-2";

    @Autowired
    private PinMappingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        PinMappingEntity entity1 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        PinMappingEntity entity2 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByPinGroupId() {
        PinMappingEntity entity1 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_1)
            .pinGroupId(PIN_GROUP_ID_1)
            .build();
        underTest.save(entity1);

        PinMappingEntity entity2 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_2)
            .pinGroupId(PIN_GROUP_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByPinGroupId(PIN_GROUP_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByPinGroupId() {
        PinMappingEntity entity1 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_1)
            .pinGroupId(PIN_GROUP_ID_1)
            .build();
        underTest.save(entity1);

        PinMappingEntity entity2 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_2)
            .pinGroupId(PIN_GROUP_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByPinGroupId(PIN_GROUP_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void findByPinGroupIdAndListItemId() {
        PinMappingEntity entity = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_1)
            .pinGroupId(PIN_GROUP_ID_1)
            .listItemId(LIST_ITEM_ID_1)
            .build();
        underTest.save(entity);

        assertThat(underTest.findByPinGroupIdAndListItemId(PIN_GROUP_ID_1, LIST_ITEM_ID_1)).contains(entity);
    }

    @Test
    @Transactional
    void deleteByListItemId() {
        PinMappingEntity entity1 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_1)
            .listItemId(LIST_ITEM_ID_1)
            .build();
        underTest.save(entity1);

        PinMappingEntity entity2 = PinMappingEntity.builder()
            .pinMappingId(PIN_MAPPING_ID_2)
            .listItemId(LIST_ITEM_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByListItemId(LIST_ITEM_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}