package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.clone.FileCloneService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileBasedColumnClonerTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 234;
    private static final UUID CLONED_COLUMN_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_COLUMN_ID = UUID.randomUUID();

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private FileCloneService fileCloneService;

    @InjectMocks
    private FileBasedColumnCloner underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Dimension originalColumn;

    @Mock
    private Dimension clonedColumn;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Mock
    private File file;

    @Test
    void cloneColumn() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(originalColumn.getIndex()).willReturn(COLUMN_INDEX);
        given(originalColumn.getDimensionId()).willReturn(ORIGINAL_COLUMN_ID);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(clonedColumn);
        given(clonedColumn.getDimensionId()).willReturn(CLONED_COLUMN_ID);
        given(columnTypeFactory.create(CLONED_COLUMN_ID, USER_ID, ColumnType.FILE)).willReturn(columnTypeDto);
        given(fileDao.findByParentValidated(ORIGINAL_COLUMN_ID)).willReturn(file);

        underTest.clone(listItem, ROW_ID, originalColumn, ColumnType.FILE);

        then(dimensionDao).should().save(clonedColumn);
        then(columnTypeDao).should().save(columnTypeDto);
        then(fileCloneService).should().cloneFile(USER_ID, CLONED_COLUMN_ID, file);
    }
}