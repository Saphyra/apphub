package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistItemCreationService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistItemAdditionServiceTest {
    private static final Integer INDEX = 425;
    private static final String CONTENT = "content";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemCreationService checklistItemCreationService;

    @InjectMocks
    private ChecklistItemAdditionService underTest;

    @Test
    void nullContent() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(INDEX)
            .content(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }

    @Test
    void nullIndex() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(null)
            .content(CONTENT)
            .build();

        Throwable ex = catchThrowable(() -> underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "index", "must not be null");
    }

    @Test
    void addChecklistItem() {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .index(INDEX)
            .content(CONTENT)
            .build();

        underTest.addChecklistItem(USER_ID, LIST_ITEM_ID, request);

        then(checklistItemCreationService).should().create(USER_ID, LIST_ITEM_ID, INDEX, CONTENT, false);
    }
}