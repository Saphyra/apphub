package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.feature.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
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
class LinkRequestValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String URL = "https://example.com";

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private LinkRequestValidator underTest;

    @Test
    void nullUrl() {
        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .url(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "url", "must not be null");
    }

    @Test
    void tooLongUrl() {
        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .url("a".repeat(NotebookConstants.MAX_CONTENT_LENGTH + 1))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(USER_ID, request));

        ExceptionValidator.validateInvalidParam(ex, "url", "too long");
    }

    @Test
    void valid() {
        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .url(URL)
            .build();

        underTest.validate(USER_ID, request);

        then(listItemRequestValidator).should().validate(USER_ID, TITLE, PARENT);
    }
}