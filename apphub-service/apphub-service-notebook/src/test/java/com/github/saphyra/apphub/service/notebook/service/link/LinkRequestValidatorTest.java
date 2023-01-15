package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LinkRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String URL = "Url";
    @Mock
    private ContentValidator contentValidator;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private LinkRequestValidator underTest;

    @Test
    public void validate() {
        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .url(URL)
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
        verify(contentValidator).validate(URL, "url");
    }
}