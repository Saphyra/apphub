package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetChildrenOfCategoryTest extends BackEndTest {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final String TITLE_3 = "title-3";
    private static final String TITLE_4 = "title-4";
    private static final String TITLE_5 = "title-5";

    @Test(groups = {"be", "notebook"})
    public void getChildrenOfCategory() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        invalidType(accessTokenId);
        get(accessTokenId);
    }

    private static void invalidType(UUID accessTokenId) {
        Response response = CategoryActions.getChildrenOfCategoryResponse(getServerPort(), accessTokenId, null, Arrays.asList(ListItemType.CATEGORY.name(), "asd"));
        ResponseValidator.verifyInvalidParam(response, "type", "contains invalid argument");
    }

    private static void get(UUID accessTokenId) {
        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentId = CategoryActions.createCategory(getServerPort(), accessTokenId, parentRequest);

        CreateCategoryRequest childCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentId)
            .build();
        UUID childCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, childCategoryRequest);

        CreateCategoryRequest excludedCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_5)
            .parent(parentId)
            .build();
        UUID excludedCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, excludedCategoryRequest);

        CreateTextRequest childTextRequest = CreateTextRequest.builder()
            .title(TITLE_3)
            .content("content")
            .parent(parentId)
            .build();
        UUID childTextId = TextActions.createText(getServerPort(), accessTokenId, childTextRequest);

        CreateLinkRequest createLinkRequest = CreateLinkRequest.builder()
            .title(TITLE_4)
            .parent(parentId)
            .url("asd")
            .build();
        LinkActions.createLink(getServerPort(), accessTokenId, createLinkRequest);

        ChildrenOfCategoryResponse result = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentId, Arrays.asList(ListItemType.CATEGORY.name(), ListItemType.TEXT.name()), excludedCategoryId);

        NotebookView categoryView = NotebookView.builder()
            .id(childCategoryId)
            .title(TITLE_2)
            .type(ListItemType.CATEGORY.name())
            .parentId(parentId)
            .parentTitle(TITLE_1)
            .enabled(true)
            .build();
        NotebookView textView = NotebookView.builder()
            .id(childTextId)
            .title(TITLE_3)
            .type(ListItemType.TEXT.name())
            .parentId(parentId)
            .parentTitle(TITLE_1)
            .enabled(true)
            .build();

        assertThat(result.getParent()).isNull();
        assertThat(result.getTitle()).isEqualTo(TITLE_1);
        assertThat(result.getChildren()).containsExactlyInAnyOrder(categoryView, textView);
    }
}
