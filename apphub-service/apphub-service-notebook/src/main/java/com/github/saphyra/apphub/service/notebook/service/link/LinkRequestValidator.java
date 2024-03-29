package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LinkRequestValidator {
    private final ContentValidator contentValidator;
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(LinkRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        contentValidator.validate(request.getUrl(), "url");
    }
}
