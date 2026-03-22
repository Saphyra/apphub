package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CheckedItemFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CHECKED_ITEM_ID = UUID.randomUUID();
    @InjectMocks
    private CheckedItemFactory underTest;

    @Test
    void create() {
        CheckedItem result = underTest.create(USER_ID, CHECKED_ITEM_ID, true);

        assertThat(result.getCheckedItemId()).isEqualTo(CHECKED_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getChecked()).isTrue();
    }
}