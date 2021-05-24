package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateChecklistTableRowStatus extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @Test(dataProvider = "localeDataProvider")
    public void rowNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        Response response = NotebookActions.getUpdateChecklistTableRowStatusResponse(language, accessTokenId, listItemId, 1, false);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void check() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        NotebookActions.updateChecklistTableRowStatus(language, accessTokenId, listItemId, 0, true);

        ChecklistTableResponse tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0)).isTrue();
    }

    @Test
    public void uncheck() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        NotebookActions.updateChecklistTableRowStatus(language, accessTokenId, listItemId, 0, false);

        ChecklistTableResponse tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0)).isFalse();
    }
}
