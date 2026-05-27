package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
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
class TableColumnEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableColumnModificationService tableColumnModificationService;

    @Mock
    private TableColumnDeletionService tableColumnDeletionService;

    @Mock
    private TableColumnAdditionService tableColumnAdditionService;

    @InjectMocks
    private TableColumnEditionService underTest;

    @Test
    void processTableColumnEdition_notModified() {
        given(tableColumnDeletionService.processTableColumnDeletion(List.of(), List.of(), List.of())).willReturn(false);
        given(tableColumnAdditionService.processTableColumnAddition(LIST_ITEM_ID, 3, List.of(), List.of(), List.of(), List.of())).willReturn(false);
        given(tableColumnModificationService.processTableColumnModification(LIST_ITEM_ID, 3, List.of(), List.of(), List.of(), List.of())).willReturn(false);

        boolean result = underTest.processTableColumnEdition(LIST_ITEM_ID, List.of(), List.of(), List.of(), List.of(), 3);

        assertThat(result).isFalse();
    }

    @Test
    void processTableColumnEdition_modified() {
        List<Content> contents = new ArrayList<>();

        given(tableColumnDeletionService.processTableColumnDeletion(List.of(), List.of(), contents)).willReturn(false);
        given(tableColumnAdditionService.processTableColumnAddition(LIST_ITEM_ID, 3, List.of(), List.of(), contents, List.of())).willReturn(false);
        given(tableColumnModificationService.processTableColumnModification(LIST_ITEM_ID, 3, List.of(), List.of(), contents, List.of())).willReturn(true);

        boolean result = underTest.processTableColumnEdition(LIST_ITEM_ID, List.of(), List.of(), contents, List.of(), 3);

        assertThat(result).isTrue();
    }
}


