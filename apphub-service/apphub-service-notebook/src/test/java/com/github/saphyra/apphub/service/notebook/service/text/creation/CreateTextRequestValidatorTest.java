package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateTextRequestValidatorTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private ContentValidator contentValidator;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private CreateTextRequestValidator underTest;

    @Test
    public void validate() {
        CreateTextRequest request = CreateTextRequest.builder()
            .parent(PARENT)
            .title(TITLE)
            .content(CONTENT)
            .build();

        underTest.validate(request);

        verify(contentValidator).validate(CONTENT);
        listItemRequestValidator.validate(TITLE, PARENT);
    }
}