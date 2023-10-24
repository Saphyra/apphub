package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemModelValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateChecklistRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private ChecklistItemModelValidator checklistItemModelValidator;

    @InjectMocks
    private CreateChecklistRequestValidator underTest;

    @Mock
    private ChecklistItemModel model;

    @Test
    void validate() {
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .items(List.of(model))
            .build();

        underTest.validate(request);

        then(listItemRequestValidator).should().validate(TITLE, PARENT);
        then(checklistItemModelValidator).should().validateNew(List.of(model));
    }
}