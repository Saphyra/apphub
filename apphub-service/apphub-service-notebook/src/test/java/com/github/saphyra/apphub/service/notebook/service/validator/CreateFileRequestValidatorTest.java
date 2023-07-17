package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateFileRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private FileMetadataValidator fileMetadataValidator;

    @InjectMocks
    private CreateFileRequestValidator underTest;

    @Mock
    private CreateFileRequest createFileRequest;

    @Mock
    private FileMetadata fileMetadata;

    @Test
    void validate() {
        given(createFileRequest.getTitle()).willReturn(TITLE);
        given(createFileRequest.getParent()).willReturn(PARENT);
        given(createFileRequest.getMetadata()).willReturn(fileMetadata);

        underTest.validate(createFileRequest);

        then(listItemRequestValidator).should().validate(TITLE, PARENT);
        then(fileMetadataValidator).should().validate(fileMetadata);
    }
}