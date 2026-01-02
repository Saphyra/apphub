package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class StoredFileRepositoryTest {
    private static final String STORED_FILE_ID_1 = "stored-file-id-1";
    private static final String STORED_FILE_ID_2 = "stored-file-id-2";
    private static final String STORED_FILE_ID_3 = "stored-file-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final LocalDateTime EXPIRATION_TIME = LocalDateTime.now();

    @Autowired
    private StoredFileRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserId() {
        StoredFileEntity entity1 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);
        StoredFileEntity entity2 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        List<StoredFileEntity> result = underTest.getByUserId(USER_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByFileUploadedAndCreatedAtBefore() {
        StoredFileEntity entity1 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_1)
            .fileUploaded(true)
            .createdAt(EXPIRATION_TIME.minusSeconds(1))
            .build();
        underTest.save(entity1);
        StoredFileEntity entity2 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_2)
            .fileUploaded(false)
            .createdAt(EXPIRATION_TIME.plusSeconds(1))
            .build();
        underTest.save(entity2);
        StoredFileEntity entity3 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_3)
            .fileUploaded(false)
            .createdAt(EXPIRATION_TIME.minusSeconds(1))
            .build();
        underTest.save(entity3);

        underTest.deleteByFileUploadedAndCreatedAtBefore(false, EXPIRATION_TIME);

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity1, entity2);
    }

    @Test
    void getAllView() {
        StoredFileEntity entity1 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_1)
            .fileUploaded(true)
            .build();
        underTest.save(entity1);
        StoredFileEntity entity2 = StoredFileEntity.builder()
            .storedFileId(STORED_FILE_ID_2)
            .fileUploaded(false)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getAllView())
            .containsExactlyInAnyOrder(
                new StoredFileView(STORED_FILE_ID_1, true),
                new StoredFileView(STORED_FILE_ID_2, false)
            );
    }
}