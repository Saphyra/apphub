package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemModelValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditChecklistRequestValidatorTest {
    private static final String TITLE = "title";

    @Mock
    private TitleValidator titleValidator;

    @Mock
    private ChecklistItemModelValidator checklistItemModelValidator;

    @InjectMocks
    private EditChecklistRequestValidator underTest;

    @Mock
    private ChecklistItemModel model;

    @Test
    void nullItems() {
        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "items", "must not be null");
    }

    @Test
    void validate() {
        EditChecklistRequest request = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(model))
            .build();

        underTest.validate(request);

        then(titleValidator).should().validate(TITLE);
        then(checklistItemModelValidator).should().validate(model);
    }
}