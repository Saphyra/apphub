package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContentFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID KEY = UUID.randomUUID();
    private static final String VALUE = "content-value";
    private static final int BATCH_INDEX = 3;

    private final ContentFactory underTest = new ContentFactory();

    @Test
    void create_withKey() {
        Content result = underTest.create(LIST_ITEM_ID, KEY, VALUE);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getBatchIndex()).isNull();
        assertThat(result.get(KEY)).isEqualTo(VALUE);
        assertThat(result.isModified()).isTrue();
    }

    @Test
    void create_withBatchIndex() {
        Content result = underTest.create(LIST_ITEM_ID, BATCH_INDEX);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getBatchIndex()).isEqualTo(BATCH_INDEX);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isModified()).isFalse();
    }
}
