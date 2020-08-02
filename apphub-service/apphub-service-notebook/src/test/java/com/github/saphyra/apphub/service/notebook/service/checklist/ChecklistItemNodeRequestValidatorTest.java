package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
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
public class ChecklistItemNodeRequestValidatorTest {
    private static final Integer ORDER = 234;
    private static final String CONTENT = "content";
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CHECKLIST_ITEM_ID_STRING = "checklist-item-id";

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ContentValidator contentValidator;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ChecklistItemNodeRequestValidator underTest;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void nullChecked() {
        ChecklistItemNodeRequest request = ChecklistItemNodeRequest.builder()
            .order(ORDER)
            .checked(null)
            .content(CONTENT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        verify(contentValidator).validate(CONTENT, "node.content");

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("node.checked")).isEqualTo("must not be null");
    }

    @Test
    public void nullOrder() {
        ChecklistItemNodeRequest request = ChecklistItemNodeRequest.builder()
            .order(null)
            .checked(true)
            .content(CONTENT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        verify(contentValidator).validate(CONTENT, "node.content");

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("node.order")).isEqualTo("must not be null");
    }

    @Test
    public void checklistItemNotFound() {
        ChecklistItemNodeRequest request = ChecklistItemNodeRequest.builder()
            .checklistItemId(CHECKLIST_ITEM_ID)
            .order(ORDER)
            .checked(true)
            .content(CONTENT)
            .build();

        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(checklistItemDao.findById(CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        verify(contentValidator).validate(CONTENT, "node.content");

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
    }

    @Test
    public void valid() {
        ChecklistItemNodeRequest request = ChecklistItemNodeRequest.builder()
            .checklistItemId(CHECKLIST_ITEM_ID)
            .order(ORDER)
            .checked(true)
            .content(CONTENT)
            .build();

        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(checklistItemDao.findById(CHECKLIST_ITEM_ID_STRING)).willReturn(Optional.of(checklistItem));

        underTest.validate(request);

        verify(contentValidator).validate(CONTENT, "node.content");
    }
}