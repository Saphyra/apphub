package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.notebook.model.request.CreateFileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateFileRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final FileMetadataValidator fileMetadataValidator;

    public void validate(CreateFileRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        fileMetadataValidator.validate(request.getMetadata());
    }
}
