package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateFileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CreateFileRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final FileMetadataValidator fileMetadataValidator;

    public void validate(UUID userId, CreateFileRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());

        fileMetadataValidator.validate(request.getMetadata());
    }
}
