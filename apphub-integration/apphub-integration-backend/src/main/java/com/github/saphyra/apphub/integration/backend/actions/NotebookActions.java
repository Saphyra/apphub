package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NotebookActions {
    public static UUID createCategory(Language language, UUID accessTokenId, CreateCategoryRequest request) {
        Response response = getCreateCategoryResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
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

    public static ChildrenOfCategoryResponse getChildrenOfCategory(Language language, UUID accessTokenId, UUID categoryId, String... type) {
        Response response = getChildrenOfCategoryResponse(language, accessTokenId, categoryId, type);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChildrenOfCategoryResponse.class);
    }

    public static Response getChildrenOfCategoryResponse(Language language, UUID accessTokenId, UUID categoryId, String... type) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("categoryId", categoryId);
        queryParams.put("type", String.join(",", type));

        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_CHILDREN_OF_NOTEBOOK_CATEGORY, new HashMap<>(), queryParams));
    }

    public static void deleteListItem(Language language, UUID accessTokenId, UUID listItemId) {
        Response response = getDeleteListItemResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteListItemResponse(Language language, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.DELETE_NOTEBOOK_LIST_ITEM, "listItemId", listItemId));
    }

    public static UUID createText(Language language, UUID accessTokenId, CreateTextRequest request) {
        Response response = getCreateTextResponse(language, accessTokenId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateTextResponse(Language language, UUID accessTokenId, CreateTextRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_TEXT));
    }

    public static TextResponse getText(Language language, UUID accessTokenId, UUID textId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_TEXT, "textId", textId));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TextResponse.class);
    }

    public static void editText(Language language, UUID accessTokenId, UUID textId, EditTextRequest request) {
        Response response = getEditTextResponse(language, accessTokenId, textId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTextResponse(Language language, UUID accessTokenId, UUID textId, EditTextRequest editTextRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(editTextRequest)
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_TEXT, "textId", textId));
    }

    public static UUID createLink(Language language, UUID accessTokenId, LinkRequest request) {
        Response response = getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateLinkResponse(Language language, UUID accessTokenId, LinkRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_LINK));
    }
}
