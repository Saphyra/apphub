package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableHeadRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableJoinRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
//TODO unit test
class EditTableRequestValidator {
    private final TitleValidator titleValidator;
    private final ColumnNameValidator columnNameValidator;
    private final ContentValidator contentValidator;

    public void validate(EditTableRequest request) {
        titleValidator.validate(request.getTitle());

        ValidationUtil.notNull(request.getTableHeads(), "tableHeads");

        request.getTableHeads()
            .forEach(this::validateTableHead);

        ValidationUtil.notNull(request.getColumns(), "columns");

        request.getColumns()
            .forEach(editTableJoinRequest -> validateColumn(editTableJoinRequest, request.getColumns()));
    }

    private void validateTableHead(EditTableHeadRequest editTableHeadRequest) {
        ValidationUtil.notNull(editTableHeadRequest.getTableHeadId(), "tableHeadId");
        ValidationUtil.notNull(editTableHeadRequest.getColumnIndex(), "columnIndex");
        columnNameValidator.validate(editTableHeadRequest.getColumnName());
    }

    private void validateColumn(EditTableJoinRequest editTableJoinRequest, List<EditTableJoinRequest> request) {
        contentValidator.validate(editTableJoinRequest.getContent(), "columnValue");

        boolean hasDuplication = request.stream()
            .filter(r -> !r.equals(editTableJoinRequest))
            .filter(r -> r.getRowIndex().equals(editTableJoinRequest.getRowIndex()))
            .anyMatch(r -> r.getColumnIndex().equals(editTableJoinRequest.getColumnIndex()));
        if (hasDuplication) {
            throw ExceptionFactory.notLoggedException(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PARAM,
                Map.of("columnLocation", "has duplication"),
                String.format("Multiple columns present on rowIndex %s and columnIndex %s", editTableJoinRequest.getRowIndex(), editTableJoinRequest.getColumnIndex())
            );
        }
    }
}
