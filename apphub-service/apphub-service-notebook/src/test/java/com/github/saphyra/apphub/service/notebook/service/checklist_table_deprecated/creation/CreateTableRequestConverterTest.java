package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.creation;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CreateTableRequestConverterTest {
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @InjectMocks
    private CreateTableRequestConverter underTest;

    @Test
    public void convert() {
        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        CreateTableRequest result = underTest.convert(request);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getColumnNames()).containsExactly(COLUMN_NAME);
        assertThat(result.getColumns()).containsExactly(Arrays.asList(COLUMN_VALUE));
    }
}