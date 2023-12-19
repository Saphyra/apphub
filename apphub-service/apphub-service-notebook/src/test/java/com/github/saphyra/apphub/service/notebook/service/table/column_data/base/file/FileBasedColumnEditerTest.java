package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.FileDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileBasedColumnEditerTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 24;
    private static final Object DATA = "data";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private FileDeletionService fileDeletionService;

    @Mock
    private FileSaver fileSaver;

    @InjectMocks
    private FileBasedColumnEditer underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableColumnModel model;

    @Mock
    private Dimension column;

    @Mock
    private FileMetadata fileMetadata;

    @Test
    void alreadyStored() {
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getData()).willReturn(DATA);
        given(dimensionDao.findByIdValidated(COLUMN_ID)).willReturn(column);
        given(objectMapperWrapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);
        given(fileMetadata.getStoredFileId()).willReturn(UUID.randomUUID());

        assertThat(underTest.edit(listItem, ROW_ID, model)).isEmpty();

        then(column).should().setIndex(COLUMN_INDEX);
        then(dimensionDao).should().save(column);

        then(fileDeletionService).shouldHaveNoInteractions();
        then(fileSaver).shouldHaveNoInteractions();
    }

    @Test
    void newFile() {
        given(model.getColumnId()).willReturn(COLUMN_ID);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);
        given(model.getData()).willReturn(DATA);
        given(dimensionDao.findByIdValidated(COLUMN_ID)).willReturn(column);
        given(objectMapperWrapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);
        given(fileMetadata.getStoredFileId()).willReturn(null);
        given(listItem.getUserId()).willReturn(USER_ID);
        given(column.getDimensionId()).willReturn(COLUMN_ID);

        assertThat(underTest.edit(listItem, ROW_ID, model)).isEmpty();

        then(column).should().setIndex(COLUMN_INDEX);
        then(dimensionDao).should().save(column);

        then(fileDeletionService).should().deleteFile(COLUMN_ID);
        then(fileSaver).should().saveFile(USER_ID, ROW_ID, model, column, fileMetadata);
    }
}