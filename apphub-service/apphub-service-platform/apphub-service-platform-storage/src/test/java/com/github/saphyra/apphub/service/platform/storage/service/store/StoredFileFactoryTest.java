package com.github.saphyra.apphub.service.platform.storage.service.store;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.storage.config.StorageProperties;
import com.github.saphyra.apphub.service.platform.storage.config.StoredFileProperties;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StoredFileFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "file-name";
    private static final Long SIZE = 234L;
    private static final UUID STORED_FILE_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer EXPIRATION_SECONDS = 3;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private StorageProperties storageProperties;

    @Mock
    private StoredFileProperties storedFileProperties;

    @InjectMocks
    private StoredFileFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(STORED_FILE_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(storageProperties.getType()).willReturn(Storage.FTP);
        given(storedFileProperties.getExpirationSeconds()).willReturn(EXPIRATION_SECONDS);

        StoredFile result = underTest.create(USER_ID, FILE_NAME, SIZE);

        assertThat(result.getStoredFileId()).isEqualTo(STORED_FILE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCreatedAt()).isEqualTo(CURRENT_TIME);
        assertThat(result.getExpiration()).isEqualTo(CURRENT_TIME.plusSeconds(EXPIRATION_SECONDS));
        assertThat(result.getFileName()).isEqualTo(FILE_NAME);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getStorage()).isEqualTo(Storage.FTP);
    }
}