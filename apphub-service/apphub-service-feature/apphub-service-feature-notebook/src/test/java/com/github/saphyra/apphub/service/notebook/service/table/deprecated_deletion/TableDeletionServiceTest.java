package com.github.saphyra.apphub.service.notebook.service.table.deprecated_deletion;

import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableDeletionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private TableRowDeletionService tableRowDeletionService;

    @InjectMocks
    private TableDeletionService underTest;

    @Mock
    private DeprecatedListItem listItem;

    @Mock
    private Dimension row;

    @Test
    void delete() {
        given(listItem.getListItemId()).willReturn(LIST_ITEM_ID);
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(row));

        underTest.delete(listItem);

        then(tableRowDeletionService).should().deleteRow(row);
    }
}