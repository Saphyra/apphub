package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class TitleValidator {
    public void validate(String title) {
        if (isBlank(title)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "title", "must not be null or blank"), "Title must not be null or blank");
        }
    }
}
