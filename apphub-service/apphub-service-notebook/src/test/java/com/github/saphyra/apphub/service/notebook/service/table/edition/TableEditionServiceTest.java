package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_head.EditTableTableHeadService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_join.EditTableTableJoinService;
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
public class TableEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String NEW_TITLE = "new-title";

    @Mock
    private EditTableRequestValidator editTableRequestValidator;

    @Mock
    private EditTableTableHeadService editTableTableHeadService;

    @Mock
    private EditTableTableJoinService editTableTableJoinService;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private TableEditionService underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void edit() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);

        List<KeyValuePair<String>> columnNames = Arrays.asList(new KeyValuePair<>(UUID.randomUUID(), "ads"));
        List<List<KeyValuePair<String>>> columns = Arrays.asList(Arrays.asList(new KeyValuePair<>(UUID.randomUUID(), "afda")));

        EditTableRequest request = new EditTableRequest(NEW_TITLE, columnNames, columns);

        underTest.edit(LIST_ITEM_ID, request);

        verify(listItem).setTitle(NEW_TITLE);
        verify(listItemDao).save(listItem);
        verify(editTableRequestValidator).validate(request);
        verify(editTableTableHeadService).processEditions(columnNames, listItem);
        verify(editTableTableJoinService).processEditions(columns, listItem);
    }
}