package com.github.saphyra.apphub.service.notebook.service.link;

import com.github.saphyra.apphub.api.feature.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class LinkRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(LinkRequest request) {
        listItemRequestValidator.validate(null, request.getTitle(), request.getParent());
        ValidationUtil.notNull(request.getUrl(), "url");
    }
}
