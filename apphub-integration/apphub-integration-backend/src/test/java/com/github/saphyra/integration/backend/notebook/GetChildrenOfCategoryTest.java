package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.NotebookView;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class GetChildrenOfCategoryTest extends TestBase {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final String TITLE_3 = "title-3";
    private static final String TITLE_4 = "title-4";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void invalidType(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getChildrenOfCategoryResponse(language, accessTokenId, null, String.join(",", ListItemType.CATEGORY.name(), "asd"));

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("type")).isEqualTo("contains invalid argument");
    }

    @Test
    public void getChildren() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, parentRequest);

        CreateCategoryRequest childCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentId)
            .build();
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, childCategoryRequest);

        CreateCategoryRequest childTextRequest = CreateCategoryRequest.builder()
            .title(TITLE_3)
            .parent(parentId)
            .build();
        UUID childTextId = NotebookActions.createCategory(language, accessTokenId, childTextRequest);
        DatabaseUtil.setListItemTypeById(childTextId, ListItemType.TEXT);

        CreateCategoryRequest childChecklistRequest = CreateCategoryRequest.builder()
            .title(TITLE_4)
            .parent(parentId)
            .build();
        UUID childChecklistId = NotebookActions.createCategory(language, accessTokenId, childChecklistRequest);
        DatabaseUtil.setListItemTypeById(childChecklistId, ListItemType.CHECKLIST);

        ChildrenOfCategoryResponse result = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId, String.join(",", ListItemType.CATEGORY.name(), ListItemType.TEXT.name()));

        NotebookView categoryView = NotebookView.builder()
            .id(childCategoryId)
            .title(TITLE_2)
            .type(ListItemType.CATEGORY.name())
            .build();
        NotebookView textView = NotebookView.builder()
            .id(childTextId)
            .title(TITLE_3)
            .type(ListItemType.TEXT.name())
            .build();

        assertThat(result.getParent()).isNull();
        assertThat(result.getTitle()).isEqualTo(TITLE_1);
        assertThat(result.getChildren()).containsExactlyInAnyOrder(categoryView, textView);
    }
}
