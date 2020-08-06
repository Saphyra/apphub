package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
//TODO unit test
public class RowValidator {
    private final ContentValidator contentValidator;

    public void validate(List<String> columnValues, int columnAmount) {
        if (columnValues.size() != columnAmount) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "columns", "amount different"), "Different amount of columns");
        }

        columnValues.forEach(s -> contentValidator.validate(s, "columnValue"));
    }
}
