package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCheckedChecklistTableItemsTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String CHECKED_COLUMN = "checked-column";
    private static final String UNCHECKED_COLUMN = "unchecked-column";

    @Test
    public void deleteCheckedItems() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(
                ChecklistTableRowRequest.<String>builder()
                    .checked(true)
                    .columns(Arrays.asList(CHECKED_COLUMN))
                    .build(),
                ChecklistTableRowRequest.<String>builder()
                    .checked(false)
                    .columns(Arrays.asList(UNCHECKED_COLUMN))
                    .build()
            ))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        Response response = NotebookActions.getDeleteCheckedChecklistTableItemsResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTableColumns()).hasSize(1);
        assertThat(checklistTableResponse.getTableColumns().get(0).getContent()).isEqualTo(UNCHECKED_COLUMN);
    }
}
