package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
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
class TableHeadModificationServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TableHeadModificationService underTest;

    @Test
    void processTableHeadModification() {
        TableHead tableHead = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .index(1)
            .build();
        Content oldContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(TABLE_HEAD_ID, "old");
        Content newContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(TABLE_HEAD_ID, "new");
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .columnIndex(3)
            .content("new")
            .type(ItemType.EXISTING)
            .build();
        List<Content> contents = new ArrayList<>(List.of(oldContent));

        given(contentFactory.create(LIST_ITEM_ID, TABLE_HEAD_ID, "new")).willReturn(newContent);

        boolean result = underTest.processTableHeadModification(LIST_ITEM_ID, List.of(model), List.of(tableHead), contents);

        assertThat(result).isTrue();
        assertThat(tableHead.getIndex()).isEqualTo(3);
        assertThat(oldContent.contains(TABLE_HEAD_ID)).isFalse();
        assertThat(contents).contains(newContent);
    }

    @Test
    void processTableHeadModification_notModified() {
        TableHead tableHead = TableHead.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .index(1)
            .build();
        Content content = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .build()
            .add(TABLE_HEAD_ID, "same");
        TableHeadModel model = TableHeadModel.builder()
            .tableHeadId(TABLE_HEAD_ID)
            .columnIndex(1)
            .content("same")
            .type(ItemType.EXISTING)
            .build();

        boolean result = underTest.processTableHeadModification(LIST_ITEM_ID, List.of(model), List.of(tableHead), new ArrayList<>(List.of(content)));

        assertThat(result).isFalse();
    }
}

