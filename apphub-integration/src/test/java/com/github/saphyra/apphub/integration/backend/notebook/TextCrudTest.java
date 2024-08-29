package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.TextResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class TextCrudTest extends BackEndTest {
    private static final String CONTENT = "content";
    private static final String TITLE = "title";
    private static final String PARENT_TITLE = "parent-title";
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @Test(groups = {"be", "notebook"})
    public void blankTitle() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessTokenId);
        create_parentNotFound(accessTokenId);
        create_parentNotCategory(accessTokenId);
        create_nullContent(accessTokenId);
        UUID parentCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).build());
        UUID textId = create(accessTokenId, parentCategoryId);
        edit_blankTitle(accessTokenId, textId);
        edit_nullContent(accessTokenId, textId);
        edit(accessTokenId, textId);
        delete(accessTokenId, parentCategoryId, textId);
    }

    private static void create_blankTitle(UUID accessTokenId) {
        CreateTextRequest create_blankTitleRequest = CreateTextRequest.builder()
            .title(" ")
            .content(CONTENT)
            .build();
        Response create_blankTitleResponse = TextActions.getCreateTextResponse(getServerPort(), accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateTextRequest create_parentNotFoundRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = TextActions.getCreateTextResponse(getServerPort(), accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(UUID accessTokenId) {
        UUID notParentCategoryId = TextActions.createText(getServerPort(), accessTokenId, CreateTextRequest.builder().title("pt").content("pc").build());
        CreateTextRequest create_parentNotCategoryRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(notParentCategoryId)
            .build();
        Response create_parentNotCategoryResponse = TextActions.getCreateTextResponse(getServerPort(), accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_nullContent(UUID accessTokenId) {
        CreateTextRequest create_nullContentRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(null)
            .build();
        Response create_nullContentResponse = TextActions.getCreateTextResponse(getServerPort(), accessTokenId, create_nullContentRequest);
        verifyInvalidParam(create_nullContentResponse, "content", "must not be null");
    }

    private static UUID create(UUID accessTokenId, UUID parentCategoryId) {
        CreateTextRequest createRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(parentCategoryId)
            .build();
        UUID textId = TextActions.createText(getServerPort(), accessTokenId, createRequest);
        TextResponse textResponse = TextActions.getText(getServerPort(), accessTokenId, textId);
        assertThat(textResponse.getTextId()).isEqualTo(textId);
        assertThat(textResponse.getTitle()).isEqualTo(TITLE);
        assertThat(textResponse.getContent()).isEqualTo(CONTENT);
        ChildrenOfCategoryResponse childrenOfCategory = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentCategoryId);
        assertThat(childrenOfCategory.getChildren()).hasSize(1);
        assertThat(childrenOfCategory.getChildren().get(0).getId()).isEqualTo(textId);
        assertThat(childrenOfCategory.getChildren().get(0).getTitle()).isEqualTo(TITLE);
        assertThat(childrenOfCategory.getChildren().get(0).getType()).isEqualTo(ListItemType.TEXT.name());
        return textId;
    }

    private static void edit_blankTitle(UUID accessTokenId, UUID textId) {
        EditTextRequest edit_blankTitleRequest = EditTextRequest.builder()
            .title(" ")
            .content(NEW_CONTENT)
            .build();
        Response edit_blankTitleResponse = TextActions.getEditTextResponse(getServerPort(), accessTokenId, textId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void edit_nullContent(UUID accessTokenId, UUID textId) {
        EditTextRequest edit_nullContentRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(null)
            .build();
        Response edit_nullContentResponse = TextActions.getEditTextResponse(getServerPort(), accessTokenId, textId, edit_nullContentRequest);
        verifyInvalidParam(edit_nullContentResponse, "content", "must not be null");
    }

    private static void edit(UUID accessTokenId, UUID textId) {
        TextResponse textResponse;
        EditTextRequest editTextRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .build();
        TextActions.editText(getServerPort(), accessTokenId, textId, editTextRequest);
        textResponse = TextActions.getText(getServerPort(), accessTokenId, textId);
        assertThat(textResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(textResponse.getContent()).isEqualTo(NEW_CONTENT);
    }

    private static void delete(UUID accessTokenId, UUID parentCategoryId, UUID textId) {
        ListItemActions.deleteListItem(getServerPort(), accessTokenId, textId);
        assertThat(CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentCategoryId).getChildren()).isEmpty();
    }
}
