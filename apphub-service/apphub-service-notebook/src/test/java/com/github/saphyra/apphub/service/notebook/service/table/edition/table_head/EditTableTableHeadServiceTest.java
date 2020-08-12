package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditTableTableHeadServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTableTableHeadDeletionService editTableTableHeadDeletionService;

    @Mock
    private EditTableTableHeadUpdateService editTableTableHeadUpdateService;

    @Mock
    private EditTableTableHeadCreationService editTableTableHeadCreationService;

    @InjectMocks
    private EditTableTableHeadService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void process() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        List<KeyValuePair<String>> columnNames = Arrays.asList(new KeyValuePair<>(UUID.randomUUID(), "asd"));


        underTest.processEditions(columnNames, listItem);

        verify(editTableTableHeadDeletionService).process(columnNames, LIST_ITEM_ID);
        verify(editTableTableHeadUpdateService).process(columnNames, LIST_ITEM_ID);
        verify(editTableTableHeadCreationService).process(columnNames, listItem);
    }
}