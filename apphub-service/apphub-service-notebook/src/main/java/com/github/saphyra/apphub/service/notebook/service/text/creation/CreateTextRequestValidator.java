package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CreateTextRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    void validate(CreateTextRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        if (isNull(request.getContent())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "content", "must not be null"), "Content must not be null");
        }
    }
}
