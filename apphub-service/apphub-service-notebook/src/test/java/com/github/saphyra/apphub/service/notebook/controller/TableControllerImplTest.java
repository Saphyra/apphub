package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ConvertTableToChecklistTableService;
import com.github.saphyra.apphub.service.notebook.service.table.query.ContentTableColumnResponseProvider;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.TableEditionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableCreationService tableCreationService;

    @Mock
    private TableEditionService tableEditionService;

    @Mock
    private TableQueryService tableQueryService;

    @Mock
    private ConvertTableToChecklistTableService convertTableToChecklistTableService;

    @Mock
    private ContentTableColumnResponseProvider contentTableColumnResponseProvider;

    @InjectMocks
    private TableControllerImpl underTest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private TableResponse<String> tableResponse;

    @Test
    public void createTable() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(tableCreationService.create(createTableRequest, USER_ID, ListItemType.TABLE)).willReturn(LIST_ITEM_ID);

        OneParamResponse<UUID> result = underTest.createTable(createTableRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editTable() {
        given(tableQueryService.getTable(LIST_ITEM_ID, contentTableColumnResponseProvider)).willReturn(tableResponse);

        TableResponse<String> result = underTest.editTable(editTableRequest, LIST_ITEM_ID);

        verify(tableEditionService).edit(LIST_ITEM_ID, editTableRequest);

        assertThat(result).isEqualTo(tableResponse);
    }

    @Test
    public void getTable() {
        given(tableQueryService.getTable(LIST_ITEM_ID, contentTableColumnResponseProvider)).willReturn(tableResponse);

        TableResponse<String> result = underTest.getTable(LIST_ITEM_ID);

        assertThat(result).isEqualTo(tableResponse);
    }

    @Test
    public void convertToChecklistTable() {
        underTest.convertToChecklistTable(LIST_ITEM_ID);

        verify(convertTableToChecklistTableService).convert(LIST_ITEM_ID);
    }
}