package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.TableEditionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TableControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableCreationService tableCreationService;

    @Mock
    private TableEditionService tableEditionService;

    @Mock
    private TableQueryService tableQueryService;

    @InjectMocks
    private TableControllerImpl underTest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private TableResponse tableResponse;

    @Test
    public void createTable() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(tableCreationService.create(createTableRequest, USER_ID, ListItemType.TABLE)).willReturn(LIST_ITEM_ID);

        OneParamResponse<UUID> result = underTest.createTable(createTableRequest, accessTokenHeader);

        assertThat(result.getValue()).isEqualTo(LIST_ITEM_ID);
    }

    @Test
    public void editTable() {
        underTest.editTable(editTableRequest, LIST_ITEM_ID);

        verify(tableEditionService).edit(LIST_ITEM_ID, editTableRequest);
    }

    @Test
    public void getTable() {
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        TableResponse result = underTest.getTable(LIST_ITEM_ID);

        assertThat(result).isEqualTo(tableResponse);
    }
}