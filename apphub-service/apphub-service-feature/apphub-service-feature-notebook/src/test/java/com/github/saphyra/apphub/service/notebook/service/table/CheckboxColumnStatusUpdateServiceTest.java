package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CheckboxColumnStatusUpdateServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID OTHER_COLUMN_ID = UUID.randomUUID();
    private static final String OLD_DATA = "old-data";
    private static final String NEW_DATA = "new-data";

    @Mock
    private ColumnDataServiceProvider columnDataServiceProvider;

    @Mock
    private ContentFactory contentFactory;

    @Mock
    private ContentDao contentDao;

    @Mock
    private TableRowDao tableRowDao;

    @InjectMocks
    private CheckboxColumnStatusUpdateService underTest;

    @Mock
    private ColumnDataService columnDataService;

    @Mock
    private Content newContent;

    @Test
    void updateColumnStatus_statusNull() {
        Throwable ex = catchThrowable(() -> underTest.updateColumnStatus(LIST_ITEM_ID, ROW_ID, COLUMN_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "status", "must not be null");
    }

    @Test
    void updateColumnStatus_columnNotFound() {
        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .columns(List.of())
            .checked(false)
            .build();
        given(tableRowDao.findByIdValidated(LIST_ITEM_ID, ROW_ID)).willReturn(row);

        ExceptionValidator.validateNotFoundException(() -> underTest.updateColumnStatus(LIST_ITEM_ID, ROW_ID, COLUMN_ID, true));
    }

    @Test
    void updateColumnStatus_notCheckboxColumn() {
        TableColumn tableColumn = TableColumn.builder()
            .columnId(COLUMN_ID)
            .type(ColumnType.TEXT)
            .build();
        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .columns(List.of(tableColumn))
            .checked(false)
            .build();
        given(tableRowDao.findByIdValidated(LIST_ITEM_ID, ROW_ID)).willReturn(row);

        Throwable ex = catchThrowable(() -> underTest.updateColumnStatus(LIST_ITEM_ID, ROW_ID, COLUMN_ID, true));

        ExceptionValidator.validateInvalidParam(ex, "columnId", "not a CHECKBOX");
    }

    @Test
    void updateColumnStatus_contentNotFound() {
        TableColumn tableColumn = TableColumn.builder()
            .columnId(COLUMN_ID)
            .type(ColumnType.CHECKBOX)
            .build();
        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .columns(List.of(tableColumn))
            .checked(false)
            .build();

        given(tableRowDao.findByIdValidated(LIST_ITEM_ID, ROW_ID)).willReturn(row);
        given(columnDataServiceProvider.getForType(ColumnType.CHECKBOX)).willReturn(columnDataService);
        given(columnDataService.serialize(true)).willReturn(Optional.of(new BiWrapper<>(NEW_DATA, Optional.empty())));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of());

        ExceptionValidator.validateNotFoundException(() -> underTest.updateColumnStatus(LIST_ITEM_ID, ROW_ID, COLUMN_ID, true));
    }

    @Test
    void updateColumnStatus() {
        TableColumn tableColumn = TableColumn.builder()
            .columnId(COLUMN_ID)
            .type(ColumnType.CHECKBOX)
            .build();
        TableRow row = TableRow.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(ROW_ID)
            .columns(List.of(tableColumn))
            .checked(false)
            .build();
        Content existingContent = Content.builder()
            .listItemId(LIST_ITEM_ID)
            .content(new HashMap<>(java.util.Map.of(
                COLUMN_ID, OLD_DATA,
                OTHER_COLUMN_ID, OLD_DATA
            )))
            .build();

        given(tableRowDao.findByIdValidated(LIST_ITEM_ID, ROW_ID)).willReturn(row);
        given(columnDataServiceProvider.getForType(ColumnType.CHECKBOX)).willReturn(columnDataService);
        given(columnDataService.serialize(true)).willReturn(Optional.of(new BiWrapper<>(NEW_DATA, Optional.empty())));
        given(contentDao.getByListItemId(LIST_ITEM_ID)).willReturn(List.of(existingContent));
        given(contentFactory.create(LIST_ITEM_ID, COLUMN_ID, NEW_DATA)).willReturn(newContent);

        underTest.updateColumnStatus(LIST_ITEM_ID, ROW_ID, COLUMN_ID, true);

        assertThat(existingContent.contains(COLUMN_ID)).isFalse();
        assertThat(existingContent.contains(OTHER_COLUMN_ID)).isTrue();
        then(contentFactory).should().create(LIST_ITEM_ID, COLUMN_ID, NEW_DATA);
        then(contentDao).should().save(LIST_ITEM_ID, List.of(existingContent, newContent));
    }
}