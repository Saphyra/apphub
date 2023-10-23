package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryActions {
    public static UUID createCategory(UUID accessTokenId, CreateCategoryRequest request) {
        Response response = getCreateCategoryResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateCategoryResponse(UUID accessTokenId, CreateCategoryRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_CATEGORY));
    }

    public static List<CategoryTreeView> getCategoryTree(UUID accessTokenId) {
        return Arrays.stream(
                RequestFactory.createAuthorizedRequest(accessTokenId)
                    .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_CATEGORY_TREE))
                    .getBody()
                    .as(CategoryTreeView[].class)
            )
            .collect(Collectors.toList());
    }

    public static ChildrenOfCategoryResponse getChildrenOfCategory(UUID accessTokenId, UUID categoryId) {
        return getChildrenOfCategory(accessTokenId, categoryId, Collections.emptyList(), null);
    }

    public static ChildrenOfCategoryResponse getChildrenOfCategory(UUID accessTokenId, UUID categoryId, List<String> types, UUID exclude) {
        Response response = getChildrenOfCategoryResponse(accessTokenId, categoryId, types, exclude);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChildrenOfCategoryResponse.class);
    }

    public static Response getChildrenOfCategoryResponse(UUID accessTokenId, UUID categoryId, List<String> types) {
        return getChildrenOfCategoryResponse(accessTokenId, categoryId, types, null);
    }

    public static Response getChildrenOfCategoryResponse(UUID accessTokenId, UUID categoryId, List<String> types, UUID exclude) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("categoryId", categoryId);
        queryParams.put("type", String.join(",", types));
        queryParams.put("exclude", exclude);

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY, new HashMap<>(), queryParams));
    }
}
