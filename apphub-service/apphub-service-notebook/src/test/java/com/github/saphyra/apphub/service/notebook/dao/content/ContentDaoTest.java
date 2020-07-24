package com.github.saphyra.apphub.service.notebook.dao.content;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ContentDaoTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ContentConverter contentConverter;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentDao underTest;

    @Mock
    private Content content;

    @Mock
    private ContentEntity entity;

    @Test
    public void deleteByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        underTest.deleteByParent(PARENT);

        verify(contentRepository).deleteByParent(PARENT_STRING);
    }

    @Test
    public void findByParentValidated_notFound() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(contentRepository.findByParent(PARENT_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByParentValidated(PARENT));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void findByParentValidated() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(contentRepository.findByParent(PARENT_STRING)).willReturn(Optional.of(entity));
        given(contentConverter.convertEntity(Optional.of(entity))).willReturn(Optional.of(content));

        Content result = underTest.findByParentValidated(PARENT);

        assertThat(result).isEqualTo(content);
    }
}