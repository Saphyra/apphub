package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileBasedColumnDataServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final Object DATA = "data";

    @Mock
    private FileBasedColumnProxy proxy;

    @Mock
    private FileDao fileDao;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FileMetadataValidator fileMetadataValidator;

    private FileBasedColumnDataServiceImpl underTest;

    @Mock
    private TableFileUploadResponse fileUpload;

    @Mock
    private TableColumnModel model;

    @Mock
    private File file;

    @Mock
    private Dimension column;

    @Mock
    private ListItem listItem;

    @Mock
    private FileMetadata fileMetadata;

    @BeforeEach
    void setUp() {
        underTest = FileBasedColumnDataServiceImpl.builder()
            .columnType(ColumnType.FILE)
            .proxy(proxy)
            .fileDao(fileDao)
            .objectMapper(objectMapper)
            .fileMetadataValidator(fileMetadataValidator)
            .build();
    }

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.FILE)).isTrue();
        assertThat(underTest.canProcess(ColumnType.IMAGE)).isFalse();
    }

    @Test
    void save() {
        given(proxy.save(USER_ID, ROW_ID, model, ColumnType.FILE)).willReturn(Optional.of(fileUpload));

        assertThat(underTest.save(USER_ID, LIST_ITEM_ID, ROW_ID, model)).contains(fileUpload);
    }

    @Test
    void getData() {
        given(fileDao.findByParentValidated(COLUMN_ID)).willReturn(file);
        given(file.getStoredFileId()).willReturn(STORED_FILE_ID);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(FileMetadata.builder().storedFileId(STORED_FILE_ID).build());
    }

    @Test
    void delete() {
        underTest.delete(column);

        then(proxy).should().delete(column);
    }

    @Test
    void edit() {
        given(proxy.edit(listItem, ROW_ID, model)).willReturn(Optional.of(fileUpload));

        assertThat(underTest.edit(listItem, ROW_ID, model)).contains(fileUpload);
    }

    @Test
    void cloneColumn() {
        underTest.clone(listItem, ROW_ID, column);

        then(proxy).should().clone(listItem, ROW_ID, column, ColumnType.FILE);
    }

    @Test
    void validateData_nullData() {
        underTest.validateData(null);
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willThrow(new RuntimeException());

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "fileMetadata", "failed to parse");
    }

    @Test
    void validateData() {
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);

        underTest.validateData(DATA);

        then(fileMetadataValidator).should().validate(fileMetadata);
    }

    private static class FileBasedColumnDataServiceImpl extends FileBasedColumnDataService {
        @Builder
        FileBasedColumnDataServiceImpl(ColumnType columnType, FileBasedColumnProxy proxy, FileDao fileDao, ObjectMapper objectMapper, FileMetadataValidator fileMetadataValidator) {
            super(columnType, proxy, fileDao, objectMapper, fileMetadataValidator);
        }
    }
}