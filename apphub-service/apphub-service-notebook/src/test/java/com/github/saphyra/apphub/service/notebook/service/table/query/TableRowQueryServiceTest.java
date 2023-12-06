package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
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
class TableRowQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 245;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private TableColumnQueryService tableColumnQueryService;

    @InjectMocks
    private TableRowQueryService underTest;

    @Mock
    private Dimension row;

    @Mock
    private CheckedItem checkedItem;

    @Mock
    private TableColumnModel columnModel;

    @Test
    void getRows() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(row));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(row.getIndex()).willReturn(ROW_INDEX);
        given(checkedItemDao.findById(ROW_ID)).willReturn(Optional.of(checkedItem));
        given(checkedItem.getChecked()).willReturn(true);
        given(tableColumnQueryService.getColumns(ROW_ID)).willReturn(List.of(columnModel));

        List<TableRowModel> result = underTest.getRows(LIST_ITEM_ID);

        assertThat(result).hasSize(1);
        TableRowModel model = result.get(0);
        assertThat(model.getRowId()).isEqualTo(ROW_ID);
        assertThat(model.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(model.getChecked()).isTrue();
        assertThat(model.getItemType()).isEqualTo(ItemType.EXISTING);
        assertThat(model.getColumns()).containsExactly(columnModel);
    }
}