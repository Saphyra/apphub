package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleyRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.TextResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//TODO split
public class NotebookActions {
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

    public static void deleteListItem(UUID accessTokenId, UUID listItemId) {
        Response response = getDeleteListItemResponse(accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteListItemResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_DELETE_LIST_ITEM, "listItemId", listItemId));
    }

    public static UUID createText(UUID accessTokenId, CreateTextRequest request) {
        Response response = getCreateTextResponse(accessTokenId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateTextResponse(UUID accessTokenId, CreateTextRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_TEXT));
    }

    public static TextResponse getText(UUID accessTokenId, UUID textId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_TEXT, "listItemId", textId));

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TextResponse.class);
    }

    public static void editText(UUID accessTokenId, UUID textId, EditTextRequest request) {
        Response response = getEditTextResponse(accessTokenId, textId, request);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTextResponse(UUID accessTokenId, UUID textId, EditTextRequest editTextRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editTextRequest)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));
    }

    public static UUID createLink(UUID accessTokenId, CreateLinkRequest request) {
        Response response = getCreateLinkResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateLinkResponse(UUID accessTokenId, CreateLinkRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_LINK));
    }

    public static UUID createChecklist(UUID accessTokenId, CreateChecklistRequest request) {
        Response response = getCreateChecklistItemResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateChecklistItemResponse(UUID accessTokenId, CreateChecklistRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));
    }

    public static void editListItem(UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        Response response = getEditListItemResponse(accessTokenId, editListItemRequest, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditListItemResponse(UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editListItemRequest)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_LIST_ITEM, "listItemId", listItemId));
    }

    public static ChecklistResponse getChecklist(UUID accessTokenId, UUID listItemId) {
        Response response = getChecklistResponse(accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChecklistResponse.class);
    }

    public static Response getChecklistResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_CHECKLIST_ITEM, "listItemId", listItemId));
    }

    public static void editChecklist(UUID accessTokenId, EditChecklistRequest editRequest, UUID listItemId) {
        Response response = getEditChecklistResponse(accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistResponse(UUID accessTokenId, EditChecklistRequest editRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editRequest)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_CHECKLIST_ITEM, "listItemId", listItemId));
    }

    public static void updateChecklistItemStatus(UUID accessTokenId, UUID checklistItemId, boolean status) {
        Response response = getUpdateChecklistItemStatusResponse(accessTokenId, checklistItemId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistItemStatusResponse(UUID accessTokenId, UUID checklistItemId, boolean status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS, "checklistItemId", checklistItemId));
    }

    public static void createTable(UUID accessTokenId, CreateTableRequest request) {
        Response response = getCreateTableResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateTableResponse(UUID accessTokenId, CreateTableRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_TABLE));
    }

    public static void editTable(UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        Response response = getEditTableResponse(accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditTableResponse(UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editTableRequest)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));
    }

    public static TableResponse getTable(UUID accessTokenId, UUID listItemId) {
        Response response = getTableResponse(accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TableResponse.class);
    }

    public static Response getTableResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_TABLE, "listItemId", listItemId));
    }

    public static Response getCloneListItemResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_CLONE_LIST_ITEM, "listItemId", listItemId));
    }

    public static void updateChecklistTableRowStatus(UUID accessTokenId, UUID rowId, boolean status) {
        Response response = getUpdateChecklistTableRowStatusResponse(accessTokenId, rowId, status);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistTableRowStatusResponse(UUID accessTokenId, UUID rowId, boolean status) {
        Map<String, Object> pathVariables = Map.of("rowId", rowId);

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS, pathVariables));
    }

    public static Response getDeleteCheckedChecklistItemsResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST, "listItemId", listItemId));
    }

    public static Response getDeleteCheckedChecklistTableItemsResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE, "listItemId", listItemId));
    }

    public static Response orderChecklistItems(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS, "listItemId", listItemId));
    }

    public static Response getPinResponse(UUID accessTokenId, UUID listItemId, Boolean pinned) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinned))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_PIN_LIST_ITEM, "listItemId", listItemId));
    }

    public static void pin(UUID accessTokenId, UUID listItemId, Boolean pinned) {
        Response response = getPinResponse(accessTokenId, listItemId, pinned);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<NotebookView> getPinnedItems(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_PINNED_ITEMS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(NotebookView[].class))
            .collect(Collectors.toList());
    }

    public static Response getSearchResponse(UUID accessTokenId, String searchText) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(searchText))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_SEARCH));
    }

    public static List<NotebookView> search(UUID accessTokenId, String search) {
        Response response = getSearchResponse(accessTokenId, search);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static void archive(UUID accessTokenId, UUID listItemId, boolean archived) {
        Response response = getArchiveResponse(accessTokenId, listItemId, archived);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getArchiveResponse(UUID accessTokenId, UUID listItemId, Boolean archived) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(archived))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_ARCHIVE_LIST_ITEM, "listItemId", listItemId));
    }

    public static UUID createOnlyTitle(UUID accessTokenId, CreateOnlyTitleyRequest request) {
        Response response = getCreateOnlyTitleResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateOnlyTitleResponse(UUID accessTokenId, CreateOnlyTitleyRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_ONLY_TITLE));
    }

    public static Response getDeleteChecklistItemResponse(UUID accessTokenId, UUID checklistItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM, "checklistItemId", checklistItemId));
    }
}
