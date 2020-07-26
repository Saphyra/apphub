package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
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