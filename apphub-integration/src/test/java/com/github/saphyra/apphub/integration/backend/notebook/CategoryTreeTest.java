package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTreeTest extends BackEndTest {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";

    @Test(groups = {"be", "notebook"})
    public void getCategoryTree() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, parentRequest);

        CreateCategoryRequest childRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentCategoryId)
            .build();
        UUID childCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, childRequest);

        List<CategoryTreeView> result = CategoryActions.getCategoryTree(getServerPort(), accessTokenId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(parentCategoryId);
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(result.get(0).getChildren().get(0).getCategoryId()).isEqualTo(childCategoryId);
        assertThat(result.get(0).getChildren().get(0).getChildren()).isEmpty();
    }
}
