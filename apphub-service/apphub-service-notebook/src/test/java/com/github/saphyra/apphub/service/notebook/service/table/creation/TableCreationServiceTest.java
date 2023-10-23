package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableCreationRequestValidator tableCreationRequestValidator;

    @Mock
    private ListItemFactory listItemFactory;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableRowCreationService tableRowCreationService;

    @Mock
    private TableHeadCreationService tableHeadCreationService;

    @InjectMocks
    private TableCreationService underTest;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableRowModel rowModel;

    @Mock
    private ListItem listItem;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Test
    void create() {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(tableHeadModel))
            .rows(List.of(rowModel))
            .build();

        given(listItemFactory.create(USER_ID, TITLE, PARENT, ListItemType.TABLE)).willReturn(listItem);
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(tableRowCreationService.saveRows(USER_ID, LIST_ITEM_ID, List.of(rowModel), ListItemType.TABLE)).willReturn(List.of(fileUploadResponse));

        List<TableFileUploadResponse> result = underTest.create(USER_ID, request);

        assertThat(result).containsExactly(fileUploadResponse);

        then(tableCreationRequestValidator).should().validate(request);
        then(listItemDao).should().save(listItem);
        then(tableHeadCreationService).should().saveTableHeads(USER_ID, request, listItem);
    }
}