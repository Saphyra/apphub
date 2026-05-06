package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
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
    public static UUID createCategory(int serverPort, String accessToken, CreateCategoryRequest request) {
        Response response = getCreateCategoryResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateCategoryResponse(int serverPort, String accessToken, CreateCategoryRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_CATEGORY));
    }

    public static List<CategoryTreeView> getCategoryTree(int serverPort, String accessToken) {
        return Arrays.stream(
                getCategoryTreeResponse(serverPort, accessToken)
                    .getBody()
                    .as(CategoryTreeView[].class)
            )
            .collect(Collectors.toList());
    }

    public static Response getCategoryTreeResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_CATEGORY_TREE));
    }

    public static ChildrenOfCategoryResponse getChildrenOfCategory(int serverPort, String accessToken, UUID categoryId) {
        return getChildrenOfCategory(serverPort, accessToken, categoryId, Collections.emptyList(), null);
    }

    public static ChildrenOfCategoryResponse getChildrenOfCategory(int serverPort, String accessToken, UUID categoryId, List<String> types, UUID exclude) {
        Response response = getChildrenOfCategoryResponse(serverPort, accessToken, categoryId, types, exclude);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChildrenOfCategoryResponse.class);
    }

    public static Response getChildrenOfCategoryResponse(int serverPort, String accessToken, UUID categoryId, List<String> types) {
        return getChildrenOfCategoryResponse(serverPort, accessToken, categoryId, types, null);
    }

    public static Response getChildrenOfCategoryResponse(int serverPort, String accessToken, UUID categoryId, List<String> types, UUID exclude) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("categoryId", categoryId);
        queryParams.put("type", String.join(",", types));
        queryParams.put("exclude", exclude);

        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY, new HashMap<>(), queryParams));
    }
}
