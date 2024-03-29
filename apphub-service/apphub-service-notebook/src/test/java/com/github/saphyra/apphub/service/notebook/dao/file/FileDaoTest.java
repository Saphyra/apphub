package com.github.saphyra.apphub.service.notebook.dao.file;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileDaoTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String STORED_FILE_ID_STRING = "stored-file-id";
    private static final UUID STORED_FILE_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private FileConverter converter;

    @Mock
    private FileRepository repository;

    @InjectMocks
    private FileDao underTest;

    @Mock
    private File file;

    @Mock
    private FileEntity entity;

    @Test
    public void findByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.findByParent(PARENT_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(file));

        Optional<File> result = underTest.findByParent(PARENT);

        assertThat(result).contains(file);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void findByParentValidated_notFound() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.findByParent(PARENT_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByParentValidated(PARENT));


        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByParentValidated() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.findByParent(PARENT_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(file));

        File result = underTest.findByParentValidated(PARENT);

        assertThat(result).isEqualTo(file);
    }

    @Test
    void deleteByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        underTest.deleteByParent(PARENT);

        verify(repository).deleteByParent(PARENT_STRING);
    }

    @Test
    void countByStoredFileId() {
        given(uuidConverter.convertDomain(STORED_FILE_ID)).willReturn(STORED_FILE_ID_STRING);
        given(repository.countByStoredFileId(STORED_FILE_ID_STRING)).willReturn(2);

        assertThat(underTest.countByStoredFileId(STORED_FILE_ID)).isEqualTo(2);
    }
}