package com.github.saphyra.apphub.service.notebook.service.only_title;

import com.github.saphyra.apphub.api.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateOnlyTitleRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private CreateOnlyTitleRequestValidator underTest;

    @Test
    public void validate() {
        CreateOnlyTitleRequest request = CreateOnlyTitleRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
    }
}