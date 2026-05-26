package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContentDaoTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String LIST_ITEM_ID_STRING = "list-item-id";
    private static final Integer BATCH_INDEX = 0;
    private static final UUID KEY = UUID.randomUUID();

    @Mock
    private ContentRepository repository;

    @Mock
    private ContentConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ContentAggregator contentAggregator;

    @InjectMocks
    private ContentDao underTest;

    @Mock
    private Content content;

    @Mock
    private ContentEntity entity;

    @Test
    void getByListItemId() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(content));

        List<Content> result = underTest.getByListItemId(LIST_ITEM_ID);

        assertThat(result).containsExactly(content);
    }

    @Test
    void save() {
        given(contentAggregator.aggregate(LIST_ITEM_ID, List.of(content))).willReturn(List.of(content));
        given(converter.convertDomain(anyList())).willReturn(List.of(entity));

        underTest.save(LIST_ITEM_ID, List.of(content));

        then(repository).should().save(List.of(entity));
    }

    @Test
    void delete_singleKey_contentContainsKey() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(content));
        given(content.removeAll(List.of(KEY))).willReturn(true);
        given(contentAggregator.aggregate(eq(LIST_ITEM_ID), anyList())).willReturn(List.of(content));
        given(converter.convertDomain(anyList())).willReturn(List.of(entity));

        underTest.delete(LIST_ITEM_ID, KEY);

        then(repository).should().save(List.of(entity));
    }

    @Test
    void deleteKeys() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(content));
        given(content.removeAll(List.of(KEY))).willReturn(true);
        given(contentAggregator.aggregate(LIST_ITEM_ID, List.of(content))).willReturn(List.of(content));
        given(converter.convertDomain(List.of(content))).willReturn(List.of(entity));

        underTest.deleteKeys(LIST_ITEM_ID, List.of(KEY));

        then(repository).should().save(List.of(entity));
    }

    @Test
    void delete_byContents() {
        given(content.getBatchIndex()).willReturn(BATCH_INDEX);
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);

        underTest.delete(LIST_ITEM_ID, List.of(content));

        then(repository).should().delete(LIST_ITEM_ID_STRING, List.of(BATCH_INDEX));
    }

    @Test
    void delete_byListItemId() {
        given(uuidConverter.convertDomain(LIST_ITEM_ID)).willReturn(LIST_ITEM_ID_STRING);
        given(repository.getByListItemId(LIST_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(content));
        given(content.getBatchIndex()).willReturn(BATCH_INDEX);

        underTest.delete(LIST_ITEM_ID);

        then(repository).should().delete(LIST_ITEM_ID_STRING, List.of(BATCH_INDEX));
    }
}