package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StoredFileFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final String EXTENSION = "extension";
    private static final Long SIZE = 234L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private StoredFileFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(STORED_FILE_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CREATED_AT);

        StoredFile result = underTest.create(USER_ID, FILE_NAME, EXTENSION, SIZE);

        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.isFileUploaded()).isFalse();
        assertThat(result.getFileName()).isEqualTo(FILE_NAME);
        assertThat(result.getExtension()).isEqualTo(EXTENSION);
        assertThat(result.getSize()).isEqualTo(SIZE);
    }
}