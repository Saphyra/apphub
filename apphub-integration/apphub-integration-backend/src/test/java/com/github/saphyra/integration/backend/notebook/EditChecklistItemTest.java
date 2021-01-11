package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class EditChecklistItemTest extends TestBase {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final Integer ORIGINAL_ORDER = 432523;
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final Integer NEW_ORDER = 456324;
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        UUID checklistItemId = checklistResponse.getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullContent(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        UUID checklistItemId = checklistResponse.getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(true)
            .content(null)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.content")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullChecked(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        UUID checklistItemId = checklistResponse.getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(null)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.checked")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullOrder(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        UUID checklistItemId = checklistResponse.getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(null)
            .checked(true)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.order")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(UUID.randomUUID())
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        Response response = NotebookActions.getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void checklistItemDeleted() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Collections.emptyList())
            .build();

        NotebookActions.editChecklistItem(language, accessTokenId, editRequest, listItemId);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(checklistResponse.getNodes()).isEmpty();
        assertThat(checklistResponse.getTitle()).isEqualTo(NEW_TITLE);
    }

    @Test
    public void checklistItemAdded() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Collections.emptyList())
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        NotebookActions.editChecklistItem(language, accessTokenId, editRequest, listItemId);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(checklistResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistResponse.getNodes()).hasSize(1);
        assertThat(checklistResponse.getNodes().get(0).getOrder()).isEqualTo(NEW_ORDER);
        assertThat(checklistResponse.getNodes().get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistResponse.getNodes().get(0).getChecked()).isTrue();
    }

    @Test
    public void checklistItemModified() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asfd")
            .build();
        UUID parent = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .parent(parent)
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(false)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        UUID checklistItemId = NotebookActions.getChecklist(language, accessTokenId, listItemId)
            .getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest node = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest editRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(node))
            .build();

        NotebookActions.editChecklistItem(language, accessTokenId, editRequest, listItemId);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(checklistResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistResponse.getNodes()).hasSize(1);
        assertThat(checklistResponse.getNodes().get(0).getOrder()).isEqualTo(NEW_ORDER);
        assertThat(checklistResponse.getNodes().get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistResponse.getNodes().get(0).getChecked()).isTrue();
    }
}
