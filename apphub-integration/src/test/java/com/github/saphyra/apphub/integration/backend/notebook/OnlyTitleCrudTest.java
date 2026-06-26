package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleRequest;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessToken);
        create_tooLongTitle(accessToken);
        create_parentNotFound(accessToken);
        UUID listItemId = create(accessToken);
        create_parentNotCategory(accessToken);
        delete(accessToken, listItemId);
    }

    private static void create_tooLongTitle(String accessToken) {
        CreateOnlyTitleRequest request = CreateOnlyTitleRequest.builder()
            .title("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
            .build();
        Response create_emptyTitleResponse = OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(create_emptyTitleResponse, "title", "too long");
    }

    private static void create_blankTitle(String accessToken) {
        CreateOnlyTitleRequest create_emptyTitleRequest = CreateOnlyTitleRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessToken, create_emptyTitleRequest);
        verifyInvalidParam(create_emptyTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(String accessToken) {
        CreateOnlyTitleRequest create_parentNotFoundRequest = CreateOnlyTitleRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessToken, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static UUID create(String accessToken) {
        CreateOnlyTitleRequest createRequest = CreateOnlyTitleRequest.builder()
            .title(TITLE)
            .build();
        UUID listItemId = OnlyTitleActions.createOnlyTitle(getServerPort(), accessToken, createRequest);

        List<NotebookView> content = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getId()).isEqualTo(listItemId);
        assertThat(content.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(content.get(0).getType()).isEqualTo(ListItemType.ONLY_TITLE.name());
        return listItemId;
    }

    private static void create_parentNotCategory(String accessToken) {
        UUID noCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateOnlyTitleRequest create_parentNotCategoryRequest = CreateOnlyTitleRequest.builder()
            .title(TITLE)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessToken, create_parentNotCategoryRequest);

        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void delete(String accessToken, UUID listItemId) {
        List<NotebookView> content;
        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);

        content = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren();
        assertThat(content).hasSize(1);
    }
}
