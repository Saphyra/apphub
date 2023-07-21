package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
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

    @Test(dataProvider = "languageDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - BlankTitle
        CreateTextRequest create_blankTitleRequest = CreateTextRequest.builder()
            .title(" ")
            .content(CONTENT)
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateTextResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(language, create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateTextRequest create_parentNotFoundRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateTextResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(language, create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Parent not category
        UUID notParentCategoryId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title("pt").content("pc").build());
        CreateTextRequest create_parentNotCategoryRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(notParentCategoryId)
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateTextResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(language, create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Create - Null Content
        CreateTextRequest create_nullContentRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(null)
            .build();
        Response create_nullContentResponse = NotebookActions.getCreateTextResponse(language, accessTokenId, create_nullContentRequest);
        verifyInvalidParam(language, create_nullContentResponse, "content", "must not be null");

        //Create
        UUID parentCategoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).build());
        CreateTextRequest createRequest = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .parent(parentCategoryId)
            .build();
        UUID textId = NotebookActions.createText(language, accessTokenId, createRequest);
        TextResponse textResponse = NotebookActions.getText(language, accessTokenId, textId);
        assertThat(textResponse.getTextId()).isEqualTo(textId);
        assertThat(textResponse.getTitle()).isEqualTo(TITLE);
        assertThat(textResponse.getContent()).isEqualTo(CONTENT);
        ChildrenOfCategoryResponse childrenOfCategory = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentCategoryId);
        assertThat(childrenOfCategory.getChildren()).hasSize(1);
        assertThat(childrenOfCategory.getChildren().get(0).getId()).isEqualTo(textId);
        assertThat(childrenOfCategory.getChildren().get(0).getTitle()).isEqualTo(TITLE);
        assertThat(childrenOfCategory.getChildren().get(0).getType()).isEqualTo(ListItemType.TEXT.name());

        //Edit - Blank title
        EditTextRequest edit_blankTitleRequest = EditTextRequest.builder()
            .title(" ")
            .content(NEW_CONTENT)
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditTextResponse(language, accessTokenId, textId, edit_blankTitleRequest);
        verifyInvalidParam(language, edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Null content
        EditTextRequest edit_nullContentRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(null)
            .build();
        Response edit_nullContentResponse = NotebookActions.getEditTextResponse(language, accessTokenId, textId, edit_nullContentRequest);
        verifyInvalidParam(language, edit_nullContentResponse, "content", "must not be null");

        //Edit
        EditTextRequest editTextRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .build();
        NotebookActions.editText(language, accessTokenId, textId, editTextRequest);
        textResponse = NotebookActions.getText(language, accessTokenId, textId);
        assertThat(textResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(textResponse.getContent()).isEqualTo(NEW_CONTENT);

        //Delete
        NotebookActions.deleteListItem(language, accessTokenId, textId);
        assertThat(NotebookActions.getChildrenOfCategory(language, accessTokenId, parentCategoryId).getChildren()).isEmpty();
    }
}
