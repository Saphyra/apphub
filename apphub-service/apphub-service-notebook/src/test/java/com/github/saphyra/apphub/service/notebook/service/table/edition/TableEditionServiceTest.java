package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableHeadRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableJoinRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_head.EditTableTableHeadService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_join.EditTableTableJoinService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    private EditTableHeadRequest editTableHeadRequest;

    @Mock
    private EditTableJoinRequest editTableJoinRequest;

    @Test
    public void edit() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);

        List<EditTableHeadRequest> columnNames = Arrays.asList(editTableHeadRequest);
        List<EditTableJoinRequest> columns = List.of(editTableJoinRequest);

        EditTableRequest request = new EditTableRequest(NEW_TITLE, columnNames, columns);

        ListItem result = underTest.edit(LIST_ITEM_ID, request);

        assertThat(result).isEqualTo(listItem);

        verify(listItem).setTitle(NEW_TITLE);
        verify(listItemDao).save(listItem);
        verify(editTableRequestValidator).validate(request);
        verify(editTableTableHeadService).processEditions(columnNames, listItem);
        verify(editTableTableJoinService).processEditions(columns, listItem);
    }
}