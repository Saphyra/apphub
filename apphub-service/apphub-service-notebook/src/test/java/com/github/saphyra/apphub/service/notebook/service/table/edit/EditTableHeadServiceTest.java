package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
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
class EditTableHeadServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTableHeadSaver editTableHeadSaver;

    @Mock
    private EditTableHeadDeleter editTableHeadDeleter;

    @InjectMocks
    private EditTableHeadService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHeadModel model;

    @Test
    void editTableHeads() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        underTest.editTableHeads(listItem, List.of(model));

        then(editTableHeadDeleter).should().deleteTableHeads(LIST_ITEM_ID, List.of(model));
        then(editTableHeadSaver).should().saveTableHeads(listItem, List.of(model));
    }
}