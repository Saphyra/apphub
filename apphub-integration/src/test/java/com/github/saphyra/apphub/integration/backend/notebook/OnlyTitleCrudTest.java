package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleyRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class OnlyTitleCrudTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(groups = {"be", "notebook"})
    public void onlyTitleCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_emptyTitle(accessTokenId);
        create_parentNotFound(accessTokenId);
        UUID listItemId = create(accessTokenId);
        create_parentNotCategory(accessTokenId);
        delete(accessTokenId, listItemId);
    }

    private static void create_emptyTitle(UUID accessTokenId) {
        CreateOnlyTitleyRequest create_emptyTitleRequest = CreateOnlyTitleyRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = OnlyTitleActions.getCreateOnlyTitleResponse(accessTokenId, create_emptyTitleRequest);
        verifyInvalidParam(create_emptyTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateOnlyTitleyRequest create_parentNotFoundRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = OnlyTitleActions.getCreateOnlyTitleResponse(accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static UUID create(UUID accessTokenId) {
        CreateOnlyTitleyRequest createRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .build();
        UUID listItemId = OnlyTitleActions.createOnlyTitle(accessTokenId, createRequest);

        List<NotebookView> content = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getId()).isEqualTo(listItemId);
        assertThat(content.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(content.get(0).getType()).isEqualTo(ListItemType.ONLY_TITLE.name());
        return listItemId;
    }

    private static void create_parentNotCategory(UUID accessTokenId) {
        UUID noCategoryParentId = TextActions.createText(accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateOnlyTitleyRequest create_parentNotCategoryRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = OnlyTitleActions.getCreateOnlyTitleResponse(accessTokenId, create_parentNotCategoryRequest);

        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void delete(UUID accessTokenId, UUID listItemId) {
        List<NotebookView> content;
        ListItemActions.deleteListItem(accessTokenId, listItemId);

        content = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren();
        assertThat(content).hasSize(1);
    }
}
