package com.github.saphyra.apphub.service.notebook.dao.file;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FileConverterTest {
    private static final UUID FILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final String FILE_ID_STRING = "file-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String STORED_FILE_ID_STRING = "stored-file-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FileConverter underTest;

    @Test
    public void convertDomain() {
        File file = File.builder()
            .fileId(FILE_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .storedFileId(STORED_FILE_ID)
            .build();

        given(uuidConverter.convertDomain(FILE_ID)).willReturn(FILE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);

        FileEntity result = underTest.convertDomain(file);

        assertThat(result.getFileId()).isEqualTo(FILE_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID_STRING);
    }

    @Test
    public void convertEntity() {
        FileEntity entity = FileEntity.builder()
            .fileId(FILE_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .storedFileId(STORED_FILE_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(FILE_ID_STRING)).willReturn(FILE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(uuidConverter.convertEntity(STORED_FILE_ID_STRING)).willReturn(STORED_FILE_ID);

        File result = underTest.convertEntity(entity);

        assertThat(result.getFileId()).isEqualTo(FILE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID);
    }
}