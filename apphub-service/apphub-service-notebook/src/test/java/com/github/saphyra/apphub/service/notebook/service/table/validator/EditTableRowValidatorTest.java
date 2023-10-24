package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableRowValidatorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private EditTableColumnValidator editTableColumnValidator;

    @InjectMocks
    private EditTableRowValidator underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Dimension row;

    @Mock
    private TableColumnModel columnModel;

    @Test
    void nullRows() {
        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "rows", "must not be null");
    }

    @Test
    void nullRowIndex() {
        TableRowModel model = TableRowModel.builder()
            .rowIndex(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowIndex", "must not be null");
    }

    @Test
    void nullChecked() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);

        TableRowModel model = TableRowModel.builder()
            .rowIndex(32)
            .checked(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.checked", "must not be null");
    }

    @Test
    void nullItemType() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);

        TableRowModel model = TableRowModel.builder()
            .rowIndex(32)
            .checked(true)
            .itemType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.itemType", "must not be null");
    }

    @Test
    void nullRowId() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);

        TableRowModel model = TableRowModel.builder()
            .rowId(null)
            .rowIndex(32)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowId", "must not be null");
    }

    @Test
    void differentExternalReference() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(dimensionDao.findByIdValidated(ROW_ID)).willReturn(row);
        given(row.getExternalReference()).willReturn(UUID.randomUUID());

        TableRowModel model = TableRowModel.builder()
            .rowId(ROW_ID)
            .rowIndex(32)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validateTableRows(LIST_ITEM_ID, List.of(model)));

        ExceptionValidator.validateInvalidParam(ex, "row.rowId", "points to different table");
    }

    @Test
    void valid() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST_TABLE);
        given(dimensionDao.findByIdValidated(ROW_ID)).willReturn(row);
        given(row.getExternalReference()).willReturn(LIST_ITEM_ID);

        TableRowModel model = TableRowModel.builder()
            .rowId(ROW_ID)
            .rowIndex(32)
            .checked(true)
            .itemType(ItemType.EXISTING)
            .columns(List.of(columnModel))
            .build();

        underTest.validateTableRows(LIST_ITEM_ID, List.of(model));

        then(editTableColumnValidator).should().validateColumns(ROW_ID, ItemType.EXISTING, List.of(columnModel));
    }
}