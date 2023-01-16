package com.github.saphyra.apphub.service.notebook.service.checklist_table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistTableCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableCreationService tableCreationService;

    @Mock
    private CreateTableRequestConverter createTableRequestConverter;

    @Mock
    private ChecklistTableRowFactory rowFactory;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @InjectMocks
    private ChecklistTableCreationService underTest;

    @Mock
    private CreateChecklistTableRequest createChecklistTableRequest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Test
    public void create() {
        ChecklistTableRowRequest<String> row = ChecklistTableRowRequest.<String>builder()
            .checked(true)
            .build();
        given(createChecklistTableRequest.getRows()).willReturn(Arrays.asList(row));

        given(createTableRequestConverter.convert(createChecklistTableRequest)).willReturn(createTableRequest);
        given(tableCreationService.create(createTableRequest, USER_ID, ListItemType.CHECKLIST_TABLE)).willReturn(LIST_ITEM_ID);
        given(rowFactory.create(USER_ID, LIST_ITEM_ID, 0, true)).willReturn(checklistTableRow);

        UUID result = underTest.create(USER_ID, createChecklistTableRequest);

        assertThat(result).isEqualTo(LIST_ITEM_ID);
        verify(checklistTableRowDao).save(checklistTableRow);
    }
}