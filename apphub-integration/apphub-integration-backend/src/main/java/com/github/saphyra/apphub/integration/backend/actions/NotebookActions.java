package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
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

    public static ChildrenOfCategoryResponse getChildrenOfCategory(Language language, UUID accessTokenId, UUID categoryId) {
        return getChildrenOfCategory(language, accessTokenId, categoryId, Collections.emptyList(), null);
    }

    public static ChildrenOfCategoryResponse getChildrenOfCategory(Language language, UUID accessTokenId, UUID categoryId, List<String> types, UUID exclude) {
        Response response = getChildrenOfCategoryResponse(language, accessTokenId, categoryId, types, exclude);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChildrenOfCategoryResponse.class);
    }

    public static Response getChildrenOfCategoryResponse(Language language, UUID accessTokenId, UUID categoryId, List<String> types) {
        return getChildrenOfCategoryResponse(language, accessTokenId, categoryId, types, null);
    }

    public static Response getChildrenOfCategoryResponse(Language language, UUID accessTokenId, UUID categoryId, List<String> types, UUID exclude) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("categoryId", categoryId);
        queryParams.put("type", String.join(",", types));
        queryParams.put("exclude", exclude);

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
            .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_TEXT, "listItemId", textId));

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
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_TEXT, "listItemId", textId));
    }

    public static UUID createLink(Language language, UUID accessTokenId, CreateLinkRequest request) {
        Response response = getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateLinkResponse(Language language, UUID accessTokenId, CreateLinkRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_LINK));
    }

    public static UUID createChecklistItem(Language language, UUID accessTokenId, CreateChecklistItemRequest request) {
        Response response = getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateChecklistItemResponse(Language language, UUID accessTokenId, CreateChecklistItemRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
    }

    public static void editListItem(Language language, UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        Response response = getEditListItemResponse(language, accessTokenId, editListItemRequest, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditListItemResponse(Language language, UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(editListItemRequest)
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", listItemId));
    }

    public static ChecklistResponse getChecklist(Language language, UUID accessTokenId, UUID listItemId) {
        Response response = getChecklistResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChecklistResponse.class);
    }

    public static Response getChecklistResponse(Language language, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_CHECKLIST, "listItemId", listItemId));
    }

    public static void editChecklistItem(Language language, UUID accessTokenId, EditChecklistItemRequest editRequest, UUID listItemId) {
        Response response = getEditChecklistResponse(language, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistResponse(Language language, UUID accessTokenId, EditChecklistItemRequest editRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(editRequest)
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));
    }

    public static void updateChecklistItemStatus(Language language, UUID accessTokenId, UUID checklistItemId, boolean status) {
        Response response = getUpdateChecklistItemStatusResponse(language, accessTokenId, checklistItemId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistItemStatusResponse(Language language, UUID accessTokenId, UUID checklistItemId, boolean status) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.UPDATE_CHECKLIST_ITEM_STATUS, "checklistItemId", checklistItemId));
    }

    public static UUID createTable(Language language, UUID accessTokenId, CreateTableRequest request) {
        Response response = getCreateTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateTableResponse(Language language, UUID accessTokenId, CreateTableRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_TABLE));
    }

    public static void editTable(Language language, UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        Response response = getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTableResponse(Language language, UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(editTableRequest)
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_TABLE, "listItemId", listItemId));
    }

    public static TableResponse getTable(Language language, UUID accessTokenId, UUID listItemId) {
        Response response = getTableResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TableResponse.class);
    }

    public static Response getTableResponse(Language language, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_TABLE, "listItemId", listItemId));
    }

    public static Response getCloneListItemResponse(Language language, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .post(UrlFactory.create(Endpoints.CLONE_NOTEBOOK_LIST_ITEM, "listItemId", listItemId));
    }

    public static UUID createChecklistTable(Language language, UUID accessTokenId, CreateChecklistTableRequest request) {
        Response response = getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .jsonPath()
            .getUUID("value");
    }

    public static Response getCreateChecklistTableResponse(Language language, UUID accessTokenId, CreateChecklistTableRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));
    }

    public static ChecklistTableResponse getChecklistTable(Language language, UUID accessTokenId, UUID listItemId) {
        Response response = getChecklistTableResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(ChecklistTableResponse.class);
    }

    public static Response getChecklistTableResponse(Language language, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.GET_NOTEBOOK_CHECKLIST_TABLE, "listItemId", listItemId));
    }

    public static void editChecklistTable(Language language, UUID accessTokenId, UUID listItemId, EditChecklistTableRequest request) {
        Response response = getEditChecklistTableResponse(language, accessTokenId, listItemId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistTableResponse(Language language, UUID accessTokenId, UUID listItemId, EditChecklistTableRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.EDIT_NOTEBOOK_CHECKLIST_TABLE, "listItemId", listItemId));
    }

    public static void updateChecklistTableRowStatus(Language language, UUID accessTokenId, UUID listItemId, int rowIndex, boolean status) {
        Response response = getUpdateChecklistTableRowStatusResponse(language, accessTokenId, listItemId, rowIndex, status);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistTableRowStatusResponse(Language language, UUID accessTokenId, UUID listItemId, int rowIndex, boolean status) {
        Map<String, Object> pathVariables = new HashMap<String, Object>() {{
            put("listItemId", listItemId);
            put("rowIndex", rowIndex);
        }};

        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.UPDATE_CHECKLIST_TABLE_ROW_STATUS, pathVariables));
    }
}
