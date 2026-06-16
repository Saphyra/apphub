package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.feature.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class LinkRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(UUID userId, LinkRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
        ValidationUtil.notNull(request.getUrl(), "url");
        ValidationUtil.maxLength(request.getUrl(), NotebookConstants.MAX_CONTENT_LENGTH, "url");
    }
}
