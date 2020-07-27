package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTreeTest extends TestBase {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";

    @Test
    public void getCategoryTree() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentCategoryId = NotebookActions.createCategory(language, accessTokenId, parentRequest);

        CreateCategoryRequest childRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentCategoryId)
            .build();
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, childRequest);

        List<CategoryTreeView> result = NotebookActions.getCategoryTree(language, accessTokenId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(parentCategoryId);
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(result.get(0).getChildren().get(0).getCategoryId()).isEqualTo(childCategoryId);
        assertThat(result.get(0).getChildren().get(0).getChildren()).isEmpty();
    }
}
