package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.lib.exception.UnprocessableEntityException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
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
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CreateCategoryRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private CreateCategoryRequestValidator underTest;

    @Mock
    private ListItem listItem;

    @Test
    public void blankTitle() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void nullParent() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(TITLE)
            .build();

        underTest.validate(request);

        verifyNoInteractions(listItemDao);
    }

    @Test
    public void parentNotFound() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        given(listItemDao.findById(PARENT)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
    }

    @Test
    public void parentNotCategory() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(UnprocessableEntityException.class);
        UnprocessableEntityException exception = (UnprocessableEntityException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
    }

    @Test
    public void valid() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        given(listItemDao.findById(PARENT)).willReturn(Optional.of(listItem));
        given(listItem.getType()).willReturn(ListItemType.CATEGORY);

        underTest.validate(request);

        //No exception thrown
    }
}