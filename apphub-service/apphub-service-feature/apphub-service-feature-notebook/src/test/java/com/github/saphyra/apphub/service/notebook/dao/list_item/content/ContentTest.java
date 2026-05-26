package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ContentTest {

    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID KEY = UUID.randomUUID();

    @Test
    void add_setsModified() {
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build();

        content.add(KEY, "value");

        assertThat(content.isModified()).isTrue();
        assertThat(content.get(KEY)).isEqualTo("value");
    }

    @Test
    void remove_keyExists_setsModified() {
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(new HashMap<>(Map.of(KEY, "value")))
            .modified(false)
            .build();

        content.remove(KEY);

        assertThat(content.isModified()).isTrue();
        assertThat(content.getContent()).isEmpty();
    }

    @Test
    void remove_keyNotPresent_doesNotSetModified() {
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build();

        content.remove(KEY);

        assertThat(content.isModified()).isFalse();
    }

    @Test
    void removeAll_someKeysExist_setsModified() {
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(new HashMap<>(Map.of(KEY, "value")))
            .modified(false)
            .build();

        content.removeAll(List.of(KEY));

        assertThat(content.isModified()).isTrue();
    }

    @Test
    void removeAll_noKeysExist_doesNotSetModified() {
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build();

        content.removeAll(List.of(KEY));

        assertThat(content.isModified()).isFalse();
    }
}