package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableColumnCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private ColumnDataServiceFetcher columnDataServiceFetcher;

    @InjectMocks
    private TableColumnCreationService underTest;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private Dimension row;

    @Mock
    private TableColumnModel model;

    @Mock
    private ColumnDataService columnDataService;

    @Test
    void saveColumns() {
        given(model.getColumnType()).willReturn(ColumnType.TEXT);
        given(columnDataServiceFetcher.findColumnDataService(ColumnType.TEXT)).willReturn(columnDataService);
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(columnDataService.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).willReturn(Optional.of(fileUploadResponse));

        assertThat(underTest.saveColumns(USER_ID, LIST_ITEM_ID, row, List.of(model))).containsExactly(fileUploadResponse);
    }
}