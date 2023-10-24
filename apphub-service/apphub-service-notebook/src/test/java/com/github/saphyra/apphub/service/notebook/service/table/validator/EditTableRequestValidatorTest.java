package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTableRequestValidatorTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";

    @Mock
    private TitleValidator titleValidator;

    @Mock
    private EditTableHeadValidator editTableHeadValidator;

    @Mock
    private EditTableRowValidator editTableRowValidator;

    @Mock
    private ColumnNumberAmountValidator columnNumberAmountValidator;

    @InjectMocks
    private EditTableRequestValidator underTest;

    @Mock
    private EditTableRequest request;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableRowModel rowModel;

    @Test
    void validate() {
        given(request.getTitle()).willReturn(TITLE);
        given(request.getTableHeads()).willReturn(List.of(tableHeadModel));
        given(request.getRows()).willReturn(List.of(rowModel));

        underTest.validate(LIST_ITEM_ID, request);

        then(titleValidator).should().validate(TITLE);
        then(editTableHeadValidator).should().validateTableHeads(LIST_ITEM_ID, List.of(tableHeadModel));
        then(columnNumberAmountValidator).should().validateColumnNumbersMatches(List.of(tableHeadModel), List.of(rowModel));
        then(editTableRowValidator).should().validateTableRows(LIST_ITEM_ID, List.of(rowModel));
    }
}