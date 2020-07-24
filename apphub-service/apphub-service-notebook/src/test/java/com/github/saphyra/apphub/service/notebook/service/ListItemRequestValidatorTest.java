package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.lib.exception.UnprocessableEntityException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TitleValidator titleValidator;

    @InjectMocks
    private ListItemRequestValidator underTest;

    @Mock
    private ListItem listItem;

    @After
    public void ver() {
        verify(titleValidator).validate(TITLE);
    }

    @Test
    public void nullParent() {
        underTest.validate(TITLE, null);

        //No exception thrown
    }

    @Test
    public void parentNotFound() {
        given(listItemDao.findById(PARENT)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.validate(TITLE, PARENT));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
    }

    @Test
    public void parentNotCategory() {
        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST);

        Throwable ex = catchThrowable(() -> underTest.validate(TITLE, PARENT));

        assertThat(ex).isInstanceOf(UnprocessableEntityException.class);
        UnprocessableEntityException exception = (UnprocessableEntityException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
    }

    @Test
    public void valid() {
        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CATEGORY);

        underTest.validate(TITLE, PARENT);
    }
}