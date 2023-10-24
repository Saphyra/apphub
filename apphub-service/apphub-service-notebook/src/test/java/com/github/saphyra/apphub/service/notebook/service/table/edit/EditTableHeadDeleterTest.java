package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
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
class EditTableHeadDeleterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID EXISTING_TABLE_HEAD_ID = UUID.randomUUID();
    private static final UUID TO_DELETE_TABLE_HEAD_ID = UUID.randomUUID();

    @Mock
    private TableHeadDao tableHeadDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private EditTableHeadDeleter underTest;

    @Mock
    private TableHeadModel existingModel;

    @Mock
    private TableHeadModel newModel;

    @Mock
    private TableHead tableHeadToKeep;

    @Mock
    private TableHead tableHeadToDelete;

    @Test
    void deleteTableHeads() {
        given(existingModel.getType()).willReturn(ItemType.EXISTING);
        given(newModel.getType()).willReturn(ItemType.NEW);
        given(existingModel.getTableHeadId()).willReturn(EXISTING_TABLE_HEAD_ID);
        given(tableHeadDao.getByParent(LIST_ITEM_ID)).willReturn(List.of(tableHeadToDelete, tableHeadToKeep));
        given(tableHeadToDelete.getTableHeadId()).willReturn(TO_DELETE_TABLE_HEAD_ID);
        given(tableHeadToKeep.getTableHeadId()).willReturn(EXISTING_TABLE_HEAD_ID);

        underTest.deleteTableHeads(LIST_ITEM_ID, List.of(existingModel, newModel));

        then(contentDao).should().deleteByParent(TO_DELETE_TABLE_HEAD_ID);
        then(tableHeadDao).should().delete(tableHeadToDelete);
    }
}