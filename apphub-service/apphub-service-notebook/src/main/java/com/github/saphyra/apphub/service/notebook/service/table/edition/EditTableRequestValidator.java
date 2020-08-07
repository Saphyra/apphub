package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.table.RowValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
//TODO unit test
class EditTableRequestValidator {
    private final ColumnNameValidator columnNameValidator;
    private final RowValidator rowValidator;
    private final TableHeadDao tableHeadDao;
    private final TableJoinDao tableJoinDao;

    public void validate(EditTableRequest request) {
        request.getColumnNames()
            .stream()
            .map(KeyValuePair::getValue)
            .forEach(columnNameValidator::validate);

        request.getColumns()
            .stream()
            .map(keyValuePairs -> keyValuePairs.stream().map(KeyValuePair::getValue).collect(Collectors.toList()))
            .forEach(columnValues -> rowValidator.validate(columnValues, request.getColumnNames().size()));

        request.getColumnNames()
            .stream()
            .filter(stringKeyValuePair -> !isNull(stringKeyValuePair.getKey()))
            .forEach(this::validateTableHeadExistence);

        request.getColumns()
            .stream()
            .flatMap(Collection::stream)
            .filter(stringKeyValuePair -> !isNull(stringKeyValuePair.getKey()))
            .forEach(this::validateTableJoinExistence);
    }

    private void validateTableHeadExistence(KeyValuePair<String> stringKeyValuePair) {
        if (!tableHeadDao.exists(stringKeyValuePair.getKey())) {
            throw new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "TableHead not found with id " + stringKeyValuePair.getKey());
        }
    }

    private void validateTableJoinExistence(KeyValuePair<String> stringKeyValuePair) {
        if (!tableJoinDao.exists(stringKeyValuePair.getKey())) {
            throw new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "TableJoin not found with id " + stringKeyValuePair.getKey());
        }
    }
}
