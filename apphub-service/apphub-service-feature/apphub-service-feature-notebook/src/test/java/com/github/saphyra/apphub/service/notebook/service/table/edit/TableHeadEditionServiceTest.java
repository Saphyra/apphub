package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableHeadEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private TableHeadModificationService tableHeadModificationService;

    @Mock
    private TableHeadAdditionService tableHeadAdditionService;

    @Mock
    private TableHeadDeletionService tableHeadDeletionService;

    @InjectMocks
    private TableHeadEditionService underTest;

    @Test
    void processTableHeads_modified() {
        given(tableHeadDeletionService.processTableHeadDeletion(List.of(), List.of(), List.of())).willReturn(false);
        given(tableHeadAdditionService.processTableHeadAddition(LIST_ITEM_ID, List.of(), List.of(), List.of())).willReturn(false);
        given(tableHeadModificationService.processTableHeadModification(LIST_ITEM_ID, List.of(), List.of(), List.of())).willReturn(true);

        underTest.processTableHeads(LIST_ITEM_ID, List.of(), List.of(), List.of());

        then(tableHeadDao).should().save(LIST_ITEM_ID, List.of());
    }

    @Test
    void processTableHeads_notModified() {
        List<TableHead> tableHeads = List.of(TableHead.builder().tableHeadId(UUID.randomUUID()).index(1).build());
        List<Content> contents = List.of(Content.builder().listItemId(LIST_ITEM_ID).build());

        given(tableHeadDeletionService.processTableHeadDeletion(List.of(), tableHeads, contents)).willReturn(false);
        given(tableHeadAdditionService.processTableHeadAddition(LIST_ITEM_ID, List.of(), tableHeads, contents)).willReturn(false);
        given(tableHeadModificationService.processTableHeadModification(LIST_ITEM_ID, List.of(), tableHeads, contents)).willReturn(false);

        underTest.processTableHeads(LIST_ITEM_ID, List.of(), tableHeads, contents);

        then(tableHeadDao).shouldHaveNoInteractions();
    }
}

