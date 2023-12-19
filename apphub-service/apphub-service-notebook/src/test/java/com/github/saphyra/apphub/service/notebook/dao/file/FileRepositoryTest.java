package com.github.saphyra.apphub.service.notebook.dao.file;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class FileRepositoryTest {
    private static final String FILE_ID_1 = "file-id-1";
    private static final String FILE_ID_2 = "file-id-2";
    private static final String FILE_ID_3 = "file-id-3";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";
    private static final String USER_ID_1 = "user-1";
    private static final String USER_ID_2 = "user-2";
    private static final String STORED_FILE_ID_1 = "stored-file-id-1";
    private static final String STORED_FILE_ID_2 = "stored-file-id-2";

    @Autowired
    private FileRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void findByParent() {
        FileEntity entity = FileEntity.builder()
            .fileId(FILE_ID_1)
            .parent(PARENT_1)
            .build();
        underTest.save(entity);

        Optional<FileEntity> result = underTest.findByParent(PARENT_1);

        assertThat(result).contains(entity);
    }

    @Test
    @Transactional
    public void deleteByUserid() {
        FileEntity entity1 = FileEntity.builder()
            .fileId(FILE_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        FileEntity entity2 = FileEntity.builder()
            .fileId(FILE_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    @Transactional
    void deleteByParent() {
        FileEntity entity1 = FileEntity.builder()
            .fileId(FILE_ID_1)
            .parent(PARENT_1)
            .build();
        underTest.save(entity1);

        FileEntity entity2 = FileEntity.builder()
            .fileId(FILE_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByParent(PARENT_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    void countByStoredFileId(){
        FileEntity entity1 = FileEntity.builder()
            .fileId(FILE_ID_1)
            .storedFileId(STORED_FILE_ID_1)
            .build();
        underTest.save(entity1);

        FileEntity entity2 = FileEntity.builder()
            .fileId(FILE_ID_2)
            .storedFileId(STORED_FILE_ID_1)
            .build();
        underTest.save(entity2);

        FileEntity entity3 = FileEntity.builder()
            .fileId(FILE_ID_3)
            .storedFileId(STORED_FILE_ID_2)
            .build();
        underTest.save(entity3);

        assertThat(underTest.countByStoredFileId(STORED_FILE_ID_1)).isEqualTo(2);
    }
}