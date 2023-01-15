package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @AfterEach
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

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    public void parentNotCategory() {
        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST);

        Throwable ex = catchThrowable(() -> underTest.validate(TITLE, PARENT));

        ExceptionValidator.validateInvalidType(ex);
    }

    @Test
    public void valid() {
        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CATEGORY);

        underTest.validate(TITLE, PARENT);
    }
}