package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCreationRequestValidatorTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private TableCreationRowValidator tableCreationRowValidator;

    @Mock
    private TableColumnTypeValidator tableColumnTypeValidator;

    @Mock
    private CreateTableHeadValidator createTableHeadValidator;

    @Mock
    private ColumnNumberAmountValidator columnNumberAmountValidator;

    @InjectMocks
    private TableCreationRequestValidator underTest;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableRowModel rowModel;

    @Test
    void nullListItemType() {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .listItemType(null)
            .tableHeads(List.of(tableHeadModel))
            .rows(List.of(rowModel))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "listItemType", "must not be null");

        then(listItemRequestValidator).should().validate(TITLE, PARENT);
        then(createTableHeadValidator).should().validateTableHeads(List.of(tableHeadModel));
    }

    @ParameterizedTest
    @MethodSource("nonTableListItems")
    void invalidListItemType(ListItemType listItemType) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .listItemType(listItemType)
            .tableHeads(List.of(tableHeadModel))
            .rows(List.of(rowModel))
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "listItemType", "must be one of [TABLE, CHECKLIST_TABLE, CUSTOM_TABLE]");

        then(listItemRequestValidator).should().validate(TITLE, PARENT);
        then(createTableHeadValidator).should().validateTableHeads(List.of(tableHeadModel));
    }

    @Test
    void valid() {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(tableHeadModel))
            .rows(List.of(rowModel))
            .build();

        underTest.validate(request);

        then(listItemRequestValidator).should().validate(TITLE, PARENT);
        then(createTableHeadValidator).should().validateTableHeads(List.of(tableHeadModel));
        then(tableCreationRowValidator).should().validateRows(ListItemType.TABLE, List.of(rowModel));
        then(columnNumberAmountValidator).should().validateColumnNumbersMatches(List.of(tableHeadModel), List.of(rowModel));
        then(tableColumnTypeValidator).should().validateColumnType(request);
    }

    private static Stream<Arguments> nonTableListItems() {
        return Stream.of(
            Arguments.of(ListItemType.CATEGORY),
            Arguments.of(ListItemType.TEXT),
            Arguments.of(ListItemType.CHECKLIST),
            Arguments.of(ListItemType.LINK),
            Arguments.of(ListItemType.ONLY_TITLE),
            Arguments.of(ListItemType.IMAGE),
            Arguments.of(ListItemType.FILE)
        );
    }
}