package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileBasedColumnSaverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 23;
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Object DATA = "data";

    @Mock
    private DimensionFactory dimensionFactory;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ColumnTypeFactory columnTypeFactory;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private FileSaver fileSaver;

    @InjectMocks
    private FileBasedColumnSaver underTest;

    @Mock
    private TableFileUploadResponse fileUpload;

    @Mock
    private TableColumnModel model;

    @Mock
    private Dimension column;

    @Mock
    private ColumnTypeDto columnTypeDto;

    @Mock
    private FileMetadata fileMetadata;

    @Test
    void save() {
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(dimensionFactory.create(USER_ID, ROW_ID, COLUMN_INDEX)).willReturn(column);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(columnTypeFactory.create(COLUMN_ID, USER_ID, ColumnType.FILE)).willReturn(columnTypeDto);
        given(model.getData()).willReturn(DATA);
        given(objectMapperWrapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);
        given(fileSaver.saveFile(USER_ID, ROW_ID, model, column, fileMetadata)).willReturn(Optional.of(fileUpload));

        assertThat(underTest.save(USER_ID, ROW_ID, model, ColumnType.FILE)).contains(fileUpload);

        then(dimensionDao).should().save(column);
        then(columnTypeDao).should().save(columnTypeDto);
    }
}