package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
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
class FileSaverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final Long FILE_SIZE = 234L;
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer ROW_INDEX = 425;
    private static final Integer COLUMN_INDEX = 42;

    @Mock
    private  DimensionDao dimensionDao;

    @Mock
    private  StorageProxy storageProxy;

    @Mock
    private  FileDao fileDao;

    @Mock
    private  FileFactory fileFactory;

    @InjectMocks
    private FileSaver underTest;

     @Mock
     private TableColumnModel model;

     @Mock
     private Dimension column;

     @Mock
     private FileMetadata fileMetadata;

     @Mock
     private File file;

     @Mock
     private Dimension row;

    @Test
    void saveFile(){
        given(fileMetadata.getFileName()).willReturn(FILE_NAME);
        given(fileMetadata.getSize()).willReturn(FILE_SIZE);
        given(storageProxy.createFile(FILE_NAME, FILE_SIZE)).willReturn(STORED_FILE_ID);
        given(column.getDimensionId()).willReturn(COLUMN_ID);
        given(fileFactory.create(USER_ID, COLUMN_ID, STORED_FILE_ID)).willReturn(file);
        given(dimensionDao.findByIdValidated(ROW_ID)).willReturn(row);
        given(row.getIndex()).willReturn(ROW_INDEX);
        given(model.getColumnIndex()).willReturn(COLUMN_INDEX);

        Optional<TableFileUploadResponse> result = underTest.saveFile(USER_ID, ROW_ID, model, column, fileMetadata);

        assertThat(result).isNotEmpty();

        TableFileUploadResponse fileUpload = result.get();
        assertThat(fileUpload.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(fileUpload.getColumnIndex()).isEqualTo(COLUMN_INDEX);
        assertThat(fileUpload.getStoredFileId()).isEqualTo(STORED_FILE_ID);

        then(fileDao).should().save(file);
    }
}