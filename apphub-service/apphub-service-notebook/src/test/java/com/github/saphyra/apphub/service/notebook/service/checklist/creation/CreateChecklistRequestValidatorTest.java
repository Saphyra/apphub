package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateChecklistRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;

    @InjectMocks
    private CreateChecklistItemRequestValidator underTest;

    @Mock
    private ChecklistItemNodeRequest nodeRequest;

    @Test
    public void nullNodes() {
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "nodes", "must not be null");

        verify(listItemRequestValidator).validate(TITLE, PARENT);
    }

    @Test
    public void validate() {
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(Arrays.asList(nodeRequest))
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
        verify(checklistItemNodeRequestValidator).validate(nodeRequest);
    }
}