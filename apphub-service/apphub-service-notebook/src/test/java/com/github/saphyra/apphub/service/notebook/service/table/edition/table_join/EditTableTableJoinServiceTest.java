package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EditTableTableJoinServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private EditTableTableJoinDeletionService editTableTableJoinDeletionService;

    @Mock
    private EditTableTableJoinUpdateService editTableTableJoinUpdateService;

    @Mock
    private EditTableTableJoinCreationService editTableTableJoinCreationService;

    @InjectMocks
    private EditTableTableJoinService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void processEditions() {
        List<List<KeyValuePair<String>>> columns = Arrays.asList(Arrays.asList(new KeyValuePair<>(UUID.randomUUID(), "asd")));

        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);

        underTest.processEditions(columns, listItem);

        verify(editTableTableJoinDeletionService).process(columns, LIST_ITEM_ID);
        verify(editTableTableJoinUpdateService).process(columns, LIST_ITEM_ID);
        verify(editTableTableJoinCreationService).process(columns, listItem);
    }
}