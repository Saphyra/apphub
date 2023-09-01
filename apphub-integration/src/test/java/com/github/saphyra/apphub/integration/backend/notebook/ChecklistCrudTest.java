package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistItemResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyListItemNotFound;
import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistCrudTest extends BackEndTest {
    private static final Integer ORDER = 234;
    private static final String CONTENT = "content";
    private static final String TITLE = "title";
    private static final Integer NEW_ORDER = 345;
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @Test(groups = {"be", "notebook"})
    public void checklistCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_blankTitle(accessTokenId);
        create_parentNotFound(accessTokenId);
        UUID notCategoryParentId = create_parentNotCategory(accessTokenId);
        create_nullNodes(accessTokenId);
        create_nullContent(accessTokenId);
        create_nullChecked(accessTokenId);
        create_nullOrder(accessTokenId);
        BiWrapper<UUID, UUID> creationResult = create(accessTokenId);
        UUID checklistItemId = creationResult.getEntity1();
        UUID listItemId = creationResult.getEntity2();
        listItemNotFound(accessTokenId);
        ChecklistItemNodeRequest edit_validNodeRequest = edit_blankTitle(accessTokenId, listItemId, checklistItemId);
        edit_nullContent(accessTokenId, listItemId, checklistItemId);
        edit_nullChecked(accessTokenId, listItemId, checklistItemId);
        edit_nullOrder(accessTokenId, listItemId, checklistItemId);
        edit_listItemNotFound(accessTokenId, edit_validNodeRequest);
        edit_checklistItemNotFound(accessTokenId, listItemId);
        edit_checklistItemDeleted(accessTokenId, listItemId);
        ChecklistResponse checklistItemAddedResponse = edit_checklistItemAdded(accessTokenId, listItemId);
        check_listItemNotFound(accessTokenId);
        check(accessTokenId, listItemId, checklistItemAddedResponse);
        uncheck(accessTokenId, listItemId, checklistItemAddedResponse);
        edit_checklistItemModified(accessTokenId, listItemId, checklistItemAddedResponse);
        delete_deleteChecked(accessTokenId, listItemId);
        deleteChecklist(accessTokenId, notCategoryParentId, listItemId);
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(
                ChecklistItemNodeRequest.builder()
                    .order(1)
                    .checked(true)
                    .content("A")
                    .build(),
                ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content("B")
                    .build()
            ))
            .build();
        listItemId = NotebookActions.createChecklist(accessTokenId, request);
        ChecklistResponse checklistResponse = order(accessTokenId, listItemId);
        deleteRow(accessTokenId, listItemId, checklistResponse);
    }

    private static void create_blankTitle(UUID accessTokenId) {
        CreateChecklistItemRequest create_blankTitleRequest = CreateChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateChecklistItemRequest create_parentNotFoundRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static UUID create_parentNotCategory(UUID accessTokenId) {
        UUID notCategoryParentId = NotebookActions.createText(accessTokenId, CreateTextRequest.builder().title("a").content("").build());
        CreateChecklistItemRequest create_parentNotCategoryRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
        return notCategoryParentId;
    }

    private static void create_nullNodes(UUID accessTokenId) {
        CreateChecklistItemRequest create_nullNodesRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(null)
            .build();
        Response create_nullNodesResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_nullNodesRequest);
        verifyInvalidParam(create_nullNodesResponse, "nodes", "must not be null");
    }

    private static void create_nullContent(UUID accessTokenId) {
        CreateChecklistItemRequest create_nullContentRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();
        Response create_nullContentResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_nullContentRequest);
        verifyInvalidParam(create_nullContentResponse, "node.content", "must not be null");
    }

    private static void create_nullChecked(UUID accessTokenId) {
        CreateChecklistItemRequest create_nullCheckedRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullCheckedResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_nullCheckedRequest);
        verifyInvalidParam(create_nullCheckedResponse, "node.checked", "must not be null");
    }

    private static void create_nullOrder(UUID accessTokenId) {
        CreateChecklistItemRequest create_nullOrderRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullOrderResponse = NotebookActions.getCreateChecklistItemResponse(accessTokenId, create_nullOrderRequest);
        verifyInvalidParam(create_nullOrderResponse, "node.order", "must not be null");
    }

    private BiWrapper<UUID, UUID> create(UUID accessTokenId) {
        CreateChecklistItemRequest createRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        UUID listItemId = NotebookActions.createChecklist(accessTokenId, createRequest);
        ChecklistResponse createdChecklistItemResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        UUID checklistItemId = createdChecklistItemResponse.getNodes()
            .get(0)
            .getChecklistItemId();
        assertThat(createdChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(createdChecklistItemResponse.getNodes()).hasSize(1);
        assertThat(createdChecklistItemResponse.getNodes().get(0).getChecklistItemId()).isNotNull();
        assertThat(createdChecklistItemResponse.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(createdChecklistItemResponse.getNodes().get(0).getChecked()).isTrue();
        assertThat(createdChecklistItemResponse.getNodes().get(0).getOrder()).isEqualTo(ORDER);

        return new BiWrapper<>(checklistItemId, listItemId);
    }

    private static void listItemNotFound(UUID accessTokenId) {
        Response get_listItemNotFoundResponse = NotebookActions.getChecklistResponse(accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);
    }

    private static ChecklistItemNodeRequest edit_blankTitle(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemNodeRequest edit_validNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest edit_blankTitleRequest = EditChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(edit_validNodeRequest))
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_blankTitleRequest, listItemId);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
        return edit_validNodeRequest;
    }

    private static void edit_nullContent(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemNodeRequest edit_nullContentNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(true)
            .content(null)
            .build();
        EditChecklistItemRequest edit_nullContentRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_nullContentNodeRequest))
            .build();
        Response edit_nullContentResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_nullContentRequest, listItemId);
        verifyInvalidParam(edit_nullContentResponse, "node.content", "must not be null");
    }

    private static void edit_nullChecked(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemNodeRequest edit_nullCheckedNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(null)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest edit_nullCheckedRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_nullCheckedNodeRequest))
            .build();
        Response edit_nullCheckedResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_nullCheckedRequest, listItemId);
        verifyInvalidParam(edit_nullCheckedResponse, "node.checked", "must not be null");
    }

    private static void edit_nullOrder(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemNodeRequest edit_nullOrderNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(null)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest edit_nullOrderRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_nullOrderNodeRequest))
            .build();
        Response edit_nullOrderResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_nullOrderRequest, listItemId);
        verifyInvalidParam(edit_nullOrderResponse, "node.order", "must not be null");
    }

    private static void edit_listItemNotFound(UUID accessTokenId, ChecklistItemNodeRequest edit_validNodeRequest) {
        EditChecklistItemRequest edit_listItemNotFoundRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_validNodeRequest))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(edit_listItemNotFoundResponse);
    }

    private static void edit_checklistItemNotFound(UUID accessTokenId, UUID listItemId) {
        ChecklistItemNodeRequest edit_checklistItemNotFoundNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(UUID.randomUUID())
            .order(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .build();

        EditChecklistItemRequest edit_checklistItemNotFoundRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_checklistItemNotFoundNodeRequest))
            .build();

        Response edit_checklistItemNotFoundResponse = NotebookActions.getEditChecklistResponse(accessTokenId, edit_checklistItemNotFoundRequest, listItemId);
        verifyListItemNotFound(edit_checklistItemNotFoundResponse);
    }

    private static void edit_checklistItemDeleted(UUID accessTokenId, UUID listItemId) {
        EditChecklistItemRequest edit_checklistItemDeletedRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Collections.emptyList())
            .build();
        NotebookActions.editChecklist(accessTokenId, edit_checklistItemDeletedRequest, listItemId);
        ChecklistResponse deletedChecklistItemResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        assertThat(deletedChecklistItemResponse.getNodes()).isEmpty();
        assertThat(deletedChecklistItemResponse.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static ChecklistResponse edit_checklistItemAdded(UUID accessTokenId, UUID listItemId) {
        ChecklistItemNodeRequest edit_checklistItemAddedNodeRequest = ChecklistItemNodeRequest.builder()
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest edit_checklistItemAddedRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_checklistItemAddedNodeRequest))
            .build();
        NotebookActions.editChecklist(accessTokenId, edit_checklistItemAddedRequest, listItemId);
        ChecklistResponse checklistItemAddedResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        assertThat(checklistItemAddedResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistItemAddedResponse.getNodes()).hasSize(1);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getOrder()).isEqualTo(NEW_ORDER);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getChecked()).isFalse();
        return checklistItemAddedResponse;
    }

    private static void check_listItemNotFound(UUID accessTokenId) {
        Response check_listItemNotFoundResponse = NotebookActions.getUpdateChecklistItemStatusResponse(accessTokenId, UUID.randomUUID(), true);
        verifyListItemNotFound(check_listItemNotFoundResponse);
    }

    private static void check(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        NotebookActions.updateChecklistItemStatus(accessTokenId, checklistItemAddedResponse.getNodes().get(0).getChecklistItemId(), true);
        assertThat(NotebookActions.getChecklist(accessTokenId, listItemId).getNodes().get(0).getChecked()).isTrue();
    }

    private static void uncheck(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        NotebookActions.updateChecklistItemStatus(accessTokenId, checklistItemAddedResponse.getNodes().get(0).getChecklistItemId(), false);
        assertThat(NotebookActions.getChecklist(accessTokenId, listItemId).getNodes().get(0).getChecked()).isFalse();
    }

    private static void edit_checklistItemModified(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        UUID checklistItemId;
        checklistItemId = checklistItemAddedResponse.getNodes()
            .get(0)
            .getChecklistItemId();

        ChecklistItemNodeRequest edit_checklistItemModifiedNodeRequest = ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(ORDER)
            .checked(true)
            .content(CONTENT)
            .build();
        EditChecklistItemRequest edit_checklistItemModifiedRequest = EditChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(edit_checklistItemModifiedNodeRequest))
            .build();
        NotebookActions.editChecklist(accessTokenId, edit_checklistItemModifiedRequest, listItemId);
        ChecklistResponse modifiedChecklistItemResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        assertThat(modifiedChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(modifiedChecklistItemResponse.getNodes()).hasSize(1);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getOrder()).isEqualTo(ORDER);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getChecked()).isTrue();
    }

    private static void delete_deleteChecked(UUID accessTokenId, UUID listItemId) {
        Response response = NotebookActions.getDeleteCheckedChecklistItemsResponse(accessTokenId, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
        ChecklistResponse deleteCheckedChecklistItemResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        assertThat(deleteCheckedChecklistItemResponse.getNodes()).isEmpty();
    }

    private static void deleteChecklist(UUID accessTokenId, UUID notCategoryParentId, UUID listItemId) {
        NotebookActions.deleteListItem(accessTokenId, listItemId);
        assertThat(NotebookActions.getChildrenOfCategory(accessTokenId, notCategoryParentId).getChildren()).isEmpty();
    }

    private ChecklistResponse order(UUID accessTokenId, UUID listItemId) {
        Response orderResponse = NotebookActions.orderChecklistItems(accessTokenId, listItemId);
        assertThat(orderResponse.getStatusCode()).isEqualTo(200);
        ChecklistResponse checklistResponse = NotebookActions.getChecklist(accessTokenId, listItemId);
        assertThat(checklistResponse.getNodes()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getNodes(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getNodes(), 1).getContent()).isEqualTo("B");
        return checklistResponse;
    }

    private static void deleteRow(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistResponse) {
        Response response;
        response = NotebookActions.getDeleteChecklistItemResponse(accessTokenId, checklistResponse.getNodes().get(0).getChecklistItemId());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(NotebookActions.getChecklist(accessTokenId, listItemId).getNodes())
            .extracting(ChecklistItemResponse::getChecklistItemId)
            .containsExactly(checklistResponse.getNodes().get(1).getChecklistItemId());
    }

    private ChecklistItemResponse findByOrder(List<ChecklistItemResponse> nodes, int order) {
        return nodes.stream()
            .filter(checklistItemResponse -> checklistItemResponse.getOrder().equals(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No checklistItem found with order " + 2));
    }
}
