package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FileColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String SERIALIZED = "serialized";
    private static final String FILE_NAME = "file.txt";
    private static final Long FILE_SIZE = 1024L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StorageProxy storageProxy;

    @InjectMocks
    private FileColumnDataService underTest;

    @Test
    void canProcess_file() {
        assertThat(underTest.canProcess(ColumnType.FILE)).isTrue();
    }

    @Test
    void canProcess_image() {
        assertThat(underTest.canProcess(ColumnType.IMAGE)).isTrue();
    }

    @Test
    void canProcess_other() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isFalse();
    }

    @Test
    void validateData_null() {
        underTest.validateData(null);
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willThrow(new RuntimeException("parse error"));

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "fileMetadata", "failed to parse");
    }

    @Test
    void validateData_withStoredFileId() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(STORED_FILE_ID).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);

        underTest.validateData(DATA);
    }

    @Test
    void validateData_nullFileName() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(null).fileName(null).size(FILE_SIZE).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "fileName", "must not be null");
    }

    @Test
    void validateData_nullSize() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(null).fileName(FILE_NAME).size(null).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "size", "must not be null");
    }

    @Test
    void validateData() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(null).fileName(FILE_NAME).size(FILE_SIZE).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);

        underTest.validateData(DATA);
    }

    @Test
    void deleteData() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(STORED_FILE_ID).build();
        given(objectMapper.readValue(SERIALIZED, FileMetadata.class)).willReturn(fileMetadata);

        underTest.deleteData(SERIALIZED);

        then(storageProxy).should().deleteFile(STORED_FILE_ID);
    }

    @Test
    void serialize_existingStoredFileId() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(STORED_FILE_ID).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);
        given(objectMapper.writeValueAsString(any(FileMetadata.class))).willReturn(SERIALIZED);

        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize(DATA);

        assertThat(result).isPresent();
        assertThat(result.get().getEntity1()).isEqualTo(SERIALIZED);
        assertThat(result.get().getEntity2()).contains(STORED_FILE_ID);
        then(storageProxy).shouldHaveNoInteractions();
    }

    @Test
    void serialize_newFile() {
        UUID newFileId = UUID.randomUUID();
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(null).fileName(FILE_NAME).size(FILE_SIZE).build();
        given(objectMapper.convertValue(DATA, FileMetadata.class)).willReturn(fileMetadata);
        given(storageProxy.createFile(FILE_NAME, FILE_SIZE)).willReturn(newFileId);
        given(objectMapper.writeValueAsString(any(FileMetadata.class))).willReturn(SERIALIZED);

        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize(DATA);

        assertThat(result).isPresent();
        assertThat(result.get().getEntity1()).isEqualTo(SERIALIZED);
        assertThat(result.get().getEntity2()).contains(newFileId);
    }

    @Test
    void deserialize() {
        FileMetadata fileMetadata = FileMetadata.builder().storedFileId(STORED_FILE_ID).build();
        given(objectMapper.readValue(SERIALIZED, FileMetadata.class)).willReturn(fileMetadata);

        Object result = underTest.deserialize(SERIALIZED);

        assertThat(result).isEqualTo(fileMetadata);
    }
}

