package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
//TODO unit test
public class ContentValidator {
    public void validate(String content) {
        if (isNull(content)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "content", "must not be null"), "Content must not be null");
        }
    }
}
