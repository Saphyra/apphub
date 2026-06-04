package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
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
class TextValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private TextValidator underTest;

    @Test
    void validate_request_nullContent() {
        CreateTextRequest request = CreateTextRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .content(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }

    @Test
    void validate_request_valid() {
        CreateTextRequest request = CreateTextRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .content(CONTENT)
            .build();

        underTest.validate(USER_ID, request);

        then(listItemRequestValidator).should().validate(USER_ID, TITLE, PARENT);
    }

    @Test
    void validate_content_null() {
        Throwable ex = catchThrowable(() -> underTest.validate(null,  "fieldName"));

        ExceptionValidator.validateInvalidParam(ex, "fieldName", "must not be null");
    }

    @Test
    void validate_content_valid() {
        underTest.validate(CONTENT, "fieldName");
    }
}


