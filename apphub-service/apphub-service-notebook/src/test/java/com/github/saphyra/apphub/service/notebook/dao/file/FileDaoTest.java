package com.github.saphyra.apphub.service.notebook.dao.file;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileDaoTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

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
}