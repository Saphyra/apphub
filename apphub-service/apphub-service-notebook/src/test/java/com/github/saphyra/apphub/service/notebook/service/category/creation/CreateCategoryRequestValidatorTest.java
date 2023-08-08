package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @InjectMocks
    private CreateCategoryRequestValidator underTest;

    @Test
    public void validate() {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
    }
}