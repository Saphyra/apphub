package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChecklistQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ChecklistResponseFactory checklistResponseFactory;

    @InjectMocks
    private ChecklistQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistResponse checklistResponse;

    @Test
    void notChecklist() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TABLE);

        Throwable ex = catchThrowable(() -> underTest.getChecklistResponse(LIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.GENERAL_ERROR);
    }

    @Test
    void getChecklistResponse() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.CHECKLIST);
        given(checklistResponseFactory.create(listItem)).willReturn(checklistResponse);

        ChecklistResponse result = underTest.getChecklistResponse(LIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }
}