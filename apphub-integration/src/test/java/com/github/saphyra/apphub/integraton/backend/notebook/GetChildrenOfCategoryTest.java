package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
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

import static com.github.saphyra.apphub.integration.localization.LocalizationKey.INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class GetChildrenOfCategoryTest extends BackEndTest {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final String TITLE_3 = "title-3";
    private static final String TITLE_4 = "title-4";
    private static final String TITLE_5 = "title-5";

    @Test(dataProvider = "languageDataProvider")
    public void getChildrenOfCategory(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Invalid type
        Response response = NotebookActions.getChildrenOfCategoryResponse(language, accessTokenId, null, Arrays.asList(ListItemType.CATEGORY.name(), "asd"));

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("type")).isEqualTo("contains invalid argument");

        //Get
        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, parentRequest);

        CreateCategoryRequest childCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentId)
            .build();
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, childCategoryRequest);

        CreateCategoryRequest excludedCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_5)
            .parent(parentId)
            .build();
        UUID excludedCategoryId = NotebookActions.createCategory(language, accessTokenId, excludedCategoryRequest);

        CreateTextRequest childTextRequest = CreateTextRequest.builder()
            .title(TITLE_3)
            .content("content")
            .parent(parentId)
            .build();
        UUID childTextId = NotebookActions.createText(language, accessTokenId, childTextRequest);

        CreateLinkRequest createLinkRequest = CreateLinkRequest.builder()
            .title(TITLE_4)
            .parent(parentId)
            .url("asd")
            .build();
        NotebookActions.createLink(language, accessTokenId, createLinkRequest);

        ChildrenOfCategoryResponse result = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId, Arrays.asList(ListItemType.CATEGORY.name(), ListItemType.TEXT.name()), excludedCategoryId);

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
