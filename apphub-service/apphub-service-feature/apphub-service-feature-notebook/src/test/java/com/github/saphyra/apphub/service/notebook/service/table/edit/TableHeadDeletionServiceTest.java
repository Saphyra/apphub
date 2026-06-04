package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TableHeadDeletionServiceTest {
    private static final UUID KEEP_ID = UUID.randomUUID();
    private static final UUID DELETE_ID = UUID.randomUUID();

    private final TableHeadDeletionService underTest = new TableHeadDeletionService();

    @Test
    void processTableHeadDeletion() {
        TableHead keep = TableHead.builder().tableHeadId(KEEP_ID).index(0).build();
        TableHead toDelete = TableHead.builder().tableHeadId(DELETE_ID).index(1).build();
        List<TableHead> tableHeads = new ArrayList<>(List.of(keep, toDelete));

        Content content = Content.builder()
            .listItemId(UUID.randomUUID())
            .build()
            .add(KEEP_ID, "keep")
            .add(DELETE_ID, "delete");

        boolean result = underTest.processTableHeadDeletion(
            List.of(TableHeadModel.builder().tableHeadId(KEEP_ID).build()),
            tableHeads,
            List.of(content)
        );

        assertThat(result).isTrue();
        assertThat(tableHeads).containsExactly(keep);
        assertThat(content.contains(DELETE_ID)).isFalse();
    }
}

