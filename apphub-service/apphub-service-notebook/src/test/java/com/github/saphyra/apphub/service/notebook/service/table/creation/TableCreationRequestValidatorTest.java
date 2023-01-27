package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.table.RowValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TableCreationRequestValidatorTest {
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @Mock
    private ColumnNameValidator columnNameValidator;

    @Mock
    private ListItemRequestValidator listItemRequestValidator;

    @Mock
    private RowValidator rowValidator;

    @InjectMocks
    private TableCreationRequestValidator underTest;

    @Test
    public void validate() {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();

        underTest.validate(request);

        verify(listItemRequestValidator).validate(TITLE, PARENT);
        verify(columnNameValidator).validate(COLUMN_NAME);
        verify(rowValidator).validate(Arrays.asList(COLUMN_VALUE), 1);
    }
}