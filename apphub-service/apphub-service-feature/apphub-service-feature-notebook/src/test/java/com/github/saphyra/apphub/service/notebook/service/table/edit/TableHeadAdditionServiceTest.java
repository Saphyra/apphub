package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableHeadAdditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();
    private static final String NEW_CONTENT = "new-content";

    @Mock
    private TableHeadFactory tableHeadFactory;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TableHeadAdditionService underTest;

    @Test
    void processTableHeadAddition() {
        TableHeadModel model = TableHeadModel.builder()
            .type(ItemType.NEW)
            .columnIndex(2)
            .content(NEW_CONTENT)
            .build();
        TableHead tableHead = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .index(2)
            .build();
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build();
        List<TableHead> tableHeads = new ArrayList<>();
        List<Content> contents = new ArrayList<>();

        given(tableHeadFactory.create(2)).willReturn(tableHead);
        given(contentFactory.create(LIST_ITEM_ID, TABLE_HEAD_ID, NEW_CONTENT)).willReturn(content);

        TableHeadModel existingModel = TableHeadModel.builder()
            .type(ItemType.EXISTING)
            .build();

        boolean result = underTest.processTableHeadAddition(LIST_ITEM_ID, List.of(existingModel, model), tableHeads, contents);

        assertThat(result).isTrue();
        assertThat(tableHeads).containsExactly(tableHead);
        assertThat(contents).containsExactly(content);
    }
}

