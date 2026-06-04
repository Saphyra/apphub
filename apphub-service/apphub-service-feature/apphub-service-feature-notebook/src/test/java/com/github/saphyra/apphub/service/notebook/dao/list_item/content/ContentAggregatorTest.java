package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentAggregator.SOFT_CAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContentAggregatorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID KEY = UUID.randomUUID();
    private static final String VALUE = "value";

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private ContentAggregator underTest;

    @Test
    void addToExistingContent_preferModified() {
        Content modifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .modified(true)
            .build();
        Content unmodifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(1)
            .modified(false)
            .build();
        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(KEY, VALUE);

        List<Content> result = underTest.aggregate(LIST_ITEM_ID, List.of(modifiedContent, unmodifiedContent, newContent));

        assertThat(result).containsExactly(modifiedContent);
        assertThat(modifiedContent.getContent()).containsEntry(KEY, VALUE);
    }

    @Test
    void addToUnmodifiedWhenSoftCapReached() {
        Content modifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .modified(true)
            .build()
            .add(UUID.randomUUID(), Stream.generate(() -> "a").limit(SOFT_CAP).collect(Collectors.joining()));
        Content unmodifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(1)
            .modified(false)
            .build();
        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(KEY, VALUE);

        List<Content> result = underTest.aggregate(LIST_ITEM_ID, List.of(modifiedContent, unmodifiedContent, newContent));

        assertThat(result).containsExactly(modifiedContent, unmodifiedContent);
        assertThat(unmodifiedContent.getContent()).containsEntry(KEY, VALUE);
    }

    @Test
    void createNewWhenNoAvailable() {
        Content modifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(0)
            .modified(true)
            .build()
            .add(UUID.randomUUID(), Stream.generate(() -> "a").limit(SOFT_CAP).collect(Collectors.joining()));
        Content unmodifiedContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(2)
            .content(new HashMap<>(Map.of(UUID.randomUUID(), Stream.generate(() -> "a").limit(SOFT_CAP).collect(Collectors.joining()))))
            .modified(false)
            .build();
        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(KEY, VALUE);
        Content container = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build();

        given(contentFactory.create(LIST_ITEM_ID, 1)).willReturn(container);

        List<Content> result = underTest.aggregate(LIST_ITEM_ID, List.of(modifiedContent, unmodifiedContent, newContent));

        assertThat(result).containsExactly(modifiedContent, container);
        assertThat(container.getContent()).containsEntry(KEY, VALUE);
    }
}