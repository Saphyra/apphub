package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NotebookActions {
    public static UUID createCategory(Language language, UUID accessTokenId, CreateCategoryRequest request) {
        Response response = getCreateCategoryResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        String categoryId = response.getBody().jsonPath().getString("value");
        return UUID.fromString(categoryId);
    }

    public static Response getCreateCategoryResponse(Language language, UUID accessTokenId, CreateCategoryRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_CATEGORY));
    }

    public static List<CategoryTreeView> getCategoryTree(Language language, UUID accessTokenId) {
        return Arrays.stream(
            RequestFactory.createAuthorizedRequest(language, accessTokenId)
                .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_CATEGORY_TREE))
                .getBody()
                .as(CategoryTreeView[].class)
        )
            .collect(Collectors.toList());
    }
}
