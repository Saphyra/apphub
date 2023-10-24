package com.github.saphyra.apphub.service.notebook.dao.checked_item;

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
class CheckedItemRepositoryTest {
    private static final String CHECKED_ITEM_ID_1 = "checked-item-id-1";
    private static final String CHECKED_ITEM_ID_2 = "checked-item-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private CheckedItemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        CheckedItemEntity entity1 = CheckedItemEntity.builder()
            .checkedItemId(CHECKED_ITEM_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        CheckedItemEntity entity2 = CheckedItemEntity.builder()
            .checkedItemId(CHECKED_ITEM_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}