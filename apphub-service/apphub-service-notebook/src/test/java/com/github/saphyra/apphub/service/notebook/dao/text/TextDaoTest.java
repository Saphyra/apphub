package com.github.saphyra.apphub.service.notebook.dao.text;

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
public class TextDaoTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private TextConverter textConverter;

    @Mock
    private TextRepository textRepository;

    @InjectMocks
    private TextDao underTest;

    @Mock
    private Text text;

    @Mock
    private TextEntity entity;

    @Test
    public void deleteByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        underTest.deleteByParent(PARENT);

        verify(textRepository).deleteByParent(PARENT_STRING);
    }

    @Test
    public void findByParentValidated_notFound() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(textRepository.findByParent(PARENT_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByParentValidated(PARENT));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void findByParentValidated() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(textRepository.findByParent(PARENT_STRING)).willReturn(Optional.of(entity));
        given(textConverter.convertEntity(Optional.of(entity))).willReturn(Optional.of(text));

        Text result = underTest.findByParentValidated(PARENT);

        assertThat(result).isEqualTo(text);
    }
}