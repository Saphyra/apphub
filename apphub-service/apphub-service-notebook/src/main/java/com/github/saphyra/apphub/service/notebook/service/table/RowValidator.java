package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RowValidator {
    private final ContentValidator contentValidator;

    public void validate(List<String> columnValues, int columnAmount) {
        if (columnValues.size() != columnAmount) {
            throw ExceptionFactory.invalidParam("columns", "amount different");
        }

        columnValues.forEach(s -> contentValidator.validate(s, "columnValue"));
    }
}
