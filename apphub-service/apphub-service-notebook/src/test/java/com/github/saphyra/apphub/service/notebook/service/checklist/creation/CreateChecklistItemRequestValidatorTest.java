package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateChecklistItemRequestValidatorTest {
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
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("nodes")).isEqualTo("must not be null");

        verify(listItemRequestValidator).validate(TITLE, PARENT);
    }

    @Test
    public void validate() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(Arrays.asList(nodeRequest))
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
        verify(checklistItemNodeRequestValidator).validate(nodeRequest);
    }
}