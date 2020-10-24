package com.github.saphyra.apphub.service.notebook.service.checklist_table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EditTableRequestConverterTest {
    private static final String TITLE = "title";

    @InjectMocks
    private EditTableRequestConverter underTest;

    @Mock
    private KeyValuePair<String> columnName;

    @Mock
    private KeyValuePair<String> columnValue;

    @Test
    public void convert() {
        EditChecklistTableRequest request = EditChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(columnName))
            .rows(Arrays.asList(ChecklistTableRowRequest.<KeyValuePair<String>>builder().columns(Arrays.asList(columnValue)).build()))
            .build();

        EditTableRequest result = underTest.convert(request);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getColumnNames()).containsExactly(columnName);
        assertThat(result.getColumns()).containsExactly(Arrays.asList(columnValue));
    }
}