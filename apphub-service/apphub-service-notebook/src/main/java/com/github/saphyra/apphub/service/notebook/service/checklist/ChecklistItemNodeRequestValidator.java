package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ChecklistItemNodeRequestValidator {
    private final ContentValidator contentValidator;

    public void validate(ChecklistItemNodeRequest request) {
        contentValidator.validate(request.getContent(), "node.content");

        if (isNull(request.getChecked())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "node.checked", "must not be null"), "Checked must not be null.");
        }

        if (isNull(request.getOrder())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "node.order", "must not be null"), "Order must not be null.");
        }
    }
}
