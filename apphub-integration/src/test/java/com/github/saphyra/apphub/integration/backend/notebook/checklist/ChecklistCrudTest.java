package com.github.saphyra.apphub.integration.backend.notebook.checklist;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessToken);
        create_parentNotFound(accessToken);
        UUID notCategoryParentId = create_parentNotCategory(accessToken);
        create_nullNodes(accessToken);
        create_nullContent(accessToken);
        create_nullChecked(accessToken);
        create_nullOrder(accessToken);
        BiWrapper<UUID, UUID> creationResult = create(accessToken);
        UUID checklistItemId = creationResult.getEntity1();
        UUID listItemId = creationResult.getEntity2();
        listItemNotFound(accessToken);
        ChecklistItemModel edit_validNodeRequest = edit_blankTitle(accessToken, listItemId, checklistItemId);
        edit_nullContent(accessToken, listItemId, checklistItemId);
        edit_nullChecked(accessToken, listItemId, checklistItemId);
        edit_nullOrder(accessToken, listItemId, checklistItemId);
        edit_listItemNotFound(accessToken, edit_validNodeRequest);
        edit_checklistItemNotFound(accessToken, listItemId);
        edit_checklistItemDeleted(accessToken, listItemId);
        ChecklistResponse checklistItemAddedResponse = edit_checklistItemAdded(accessToken, listItemId);
        check_listItemNotFound(accessToken, listItemId);
        check(accessToken, listItemId, checklistItemAddedResponse);
        uncheck(accessToken, listItemId, checklistItemAddedResponse);
        edit_checklistItemModified(accessToken, listItemId, checklistItemAddedResponse);
        delete_deleteChecked(accessToken, listItemId);
        deleteChecklist(accessToken, notCategoryParentId, listItemId);
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(
                ChecklistItemModel.builder()
                    .index(1)
                    .checked(true)
                    .content("A")
                    .build(),
                ChecklistItemModel.builder()
                    .index(0)
                    .checked(true)
                    .content("B")
                    .build()
            ))
            .build();
        listItemId = ChecklistActions.createChecklist(getServerPort(), accessToken, request);
        ChecklistResponse checklistResponse = order(accessToken, listItemId);
        deleteRow(accessToken, listItemId, checklistResponse);
        editChecklistItem_nullContent(accessToken, listItemId, checklistResponse.getItems().get(1).getChecklistItemId());
        editChecklistItem(accessToken, checklistResponse.getItems().get(1).getChecklistItemId(), listItemId);
    }

    private static void create_blankTitle(String accessToken) {
        CreateChecklistRequest create_blankTitleRequest = CreateChecklistRequest.builder()
            .title(" ")
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_blankTitleResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(String accessToken) {
        CreateChecklistRequest create_parentNotFoundRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotFoundResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static UUID create_parentNotCategory(String accessToken) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title("a").content("").build());
        CreateChecklistRequest create_parentNotCategoryRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotCategoryResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
        return notCategoryParentId;
    }

    private static void create_nullNodes(String accessToken) {
        CreateChecklistRequest create_nullNodesRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(null)
            .build();
        Response create_nullNodesResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_nullNodesRequest);
        verifyInvalidParam(create_nullNodesResponse, "items", "must not be null");
    }

    private static void create_nullContent(String accessToken) {
        CreateChecklistRequest create_nullContentRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();
        Response create_nullContentResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_nullContentRequest);
        verifyInvalidParam(create_nullContentResponse, "item.content", "must not be null");
    }

    private static void create_nullChecked(String accessToken) {
        CreateChecklistRequest create_nullCheckedRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullCheckedResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_nullCheckedRequest);
        verifyInvalidParam(create_nullCheckedResponse, "item.checked", "must not be null");
    }

    private static void create_nullOrder(String accessToken) {
        CreateChecklistRequest create_nullOrderRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(ChecklistItemModel.builder()
                .index(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullOrderResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, create_nullOrderRequest);
        verifyInvalidParam(create_nullOrderResponse, "item.index", "must not be null");
    }

    private BiWrapper<UUID, UUID> create(String accessToken) {
        CreateChecklistRequest createRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        UUID listItemId = ChecklistActions.createChecklist(getServerPort(), accessToken, createRequest);
        ChecklistResponse createdChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        UUID checklistItemId = createdChecklistItemResponse.getItems()
            .getFirst()
            .getChecklistItemId();
        assertThat(createdChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(createdChecklistItemResponse.getItems()).hasSize(1);
        assertThat(createdChecklistItemResponse.getItems().getFirst().getChecklistItemId()).isNotNull();
        assertThat(createdChecklistItemResponse.getItems().getFirst().getContent()).isEqualTo(CONTENT);
        assertThat(createdChecklistItemResponse.getItems().getFirst().getChecked()).isTrue();
        assertThat(createdChecklistItemResponse.getItems().getFirst().getIndex()).isEqualTo(ORDER);

        return new BiWrapper<>(checklistItemId, listItemId);
    }

    private static void listItemNotFound(String accessToken) {
        Response get_listItemNotFoundResponse = ChecklistActions.getChecklistResponse(getServerPort(), accessToken, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);
    }

    private static ChecklistItemModel edit_blankTitle(String accessToken, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_validNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .type(ItemType.EXISTING)
            .build();
        EditChecklistRequest edit_blankTitleRequest = EditChecklistRequest.builder()
            .title(" ")
            .items(List.of(edit_validNodeRequest))
            .build();
        Response edit_blankTitleResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_blankTitleRequest, listItemId);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
        return edit_validNodeRequest;
    }

    private static void edit_nullContent(String accessToken, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullContentNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(true)
            .content(null)
            .build();
        EditChecklistRequest edit_nullContentRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_nullContentNodeRequest))
            .build();
        Response edit_nullContentResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_nullContentRequest, listItemId);
        verifyInvalidParam(edit_nullContentResponse, "item.content", "must not be null");
    }

    private static void edit_nullChecked(String accessToken, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullCheckedNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(null)
            .content(NEW_CONTENT)
            .build();
        EditChecklistRequest edit_nullCheckedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_nullCheckedNodeRequest))
            .build();
        Response edit_nullCheckedResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_nullCheckedRequest, listItemId);
        verifyInvalidParam(edit_nullCheckedResponse, "item.checked", "must not be null");
    }

    private static void edit_nullOrder(String accessToken, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullOrderNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(null)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistRequest edit_nullOrderRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_nullOrderNodeRequest))
            .build();
        Response edit_nullOrderResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_nullOrderRequest, listItemId);
        verifyInvalidParam(edit_nullOrderResponse, "item.index", "must not be null");
    }

    private static void edit_listItemNotFound(String accessToken, ChecklistItemModel edit_validNodeRequest) {
        EditChecklistRequest edit_listItemNotFoundRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_validNodeRequest))
            .build();
        Response edit_listItemNotFoundResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(edit_listItemNotFoundResponse);
    }

    private static void edit_checklistItemNotFound(String accessToken, UUID listItemId) {
        ChecklistItemModel edit_checklistItemNotFoundNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(UUID.randomUUID())
            .index(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .type(ItemType.EXISTING)
            .build();

        EditChecklistRequest edit_checklistItemNotFoundRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_checklistItemNotFoundNodeRequest))
            .build();

        Response edit_checklistItemNotFoundResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, edit_checklistItemNotFoundRequest, listItemId);
        ResponseValidator.verifyErrorResponse(edit_checklistItemNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_checklistItemDeleted(String accessToken, UUID listItemId) {
        EditChecklistRequest edit_checklistItemDeletedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Collections.emptyList())
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessToken, edit_checklistItemDeletedRequest, listItemId);
        ChecklistResponse deletedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        assertThat(deletedChecklistItemResponse.getItems()).isEmpty();
        assertThat(deletedChecklistItemResponse.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static ChecklistResponse edit_checklistItemAdded(String accessToken, UUID listItemId) {
        ChecklistItemModel edit_checklistItemAddedNodeRequest = ChecklistItemModel.builder()
            .index(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .type(ItemType.NEW)
            .build();
        EditChecklistRequest edit_checklistItemAddedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(List.of(edit_checklistItemAddedNodeRequest))
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessToken, edit_checklistItemAddedRequest, listItemId);
        ChecklistResponse checklistItemAddedResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        assertThat(checklistItemAddedResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistItemAddedResponse.getItems()).hasSize(1);
        assertThat(checklistItemAddedResponse.getItems().getFirst().getIndex()).isEqualTo(NEW_ORDER);
        assertThat(checklistItemAddedResponse.getItems().getFirst().getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistItemAddedResponse.getItems().getFirst().getChecked()).isFalse();
        return checklistItemAddedResponse;
    }

    private static void check_listItemNotFound(String accessToken, UUID listItemId) {
        Response check_listItemNotFoundResponse = ChecklistActions.getUpdateChecklistItemStatusResponse(getServerPort(), accessToken, listItemId, UUID.randomUUID(), true);
        ResponseValidator.verifyErrorResponse(check_listItemNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void check(String accessToken, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        ChecklistActions.updateChecklistItemStatus(getServerPort(), accessToken, listItemId, checklistItemAddedResponse.getItems().getFirst().getChecklistItemId(), true);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId).getItems().getFirst().getChecked()).isTrue();
    }

    private static void uncheck(String accessToken, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        ChecklistActions.updateChecklistItemStatus(getServerPort(), accessToken, listItemId, checklistItemAddedResponse.getItems().getFirst().getChecklistItemId(), false);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId).getItems().getFirst().getChecked()).isFalse();
    }

    private static void edit_checklistItemModified(String accessToken, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        UUID checklistItemId;
        checklistItemId = checklistItemAddedResponse.getItems()
            .getFirst()
            .getChecklistItemId();

        ChecklistItemModel edit_checklistItemModifiedNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(ORDER)
            .checked(true)
            .content(CONTENT)
            .type(ItemType.EXISTING)
            .build();
        EditChecklistRequest edit_checklistItemModifiedRequest = EditChecklistRequest.builder()
            .title(TITLE)
            .items(List.of(edit_checklistItemModifiedNodeRequest))
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessToken, edit_checklistItemModifiedRequest, listItemId);
        ChecklistResponse modifiedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        assertThat(modifiedChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(modifiedChecklistItemResponse.getItems()).hasSize(1);
        assertThat(modifiedChecklistItemResponse.getItems().getFirst().getIndex()).isEqualTo(ORDER);
        assertThat(modifiedChecklistItemResponse.getItems().getFirst().getContent()).isEqualTo(CONTENT);
        assertThat(modifiedChecklistItemResponse.getItems().getFirst().getChecked()).isTrue();
    }

    private static void delete_deleteChecked(String accessToken, UUID listItemId) {
        Response response = ChecklistActions.getDeleteCheckedChecklistItemsResponse(getServerPort(), accessToken, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
        ChecklistResponse deleteCheckedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        assertThat(deleteCheckedChecklistItemResponse.getItems()).isEmpty();
    }

    private static void deleteChecklist(String accessToken, UUID notCategoryParentId, UUID listItemId) {
        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);
        assertThat(CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, notCategoryParentId).getChildren()).isEmpty();
    }

    private ChecklistResponse order(String accessToken, UUID listItemId) {
        Response orderResponse = ChecklistActions.getOrderItemsResponse(getServerPort(), accessToken, listItemId);
        assertThat(orderResponse.getStatusCode()).isEqualTo(200);
        ChecklistResponse checklistResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);
        assertThat(checklistResponse.getItems()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getItems(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getItems(), 1).getContent()).isEqualTo("B");
        return checklistResponse;
    }

    private static void deleteRow(String accessToken, UUID listItemId, ChecklistResponse checklistResponse) {
        Response response = ChecklistActions.getDeleteChecklistItemResponse(getServerPort(), accessToken, listItemId, checklistResponse.getItems().getFirst().getChecklistItemId());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId).getItems())
            .extracting(ChecklistItemModel::getChecklistItemId)
            .containsExactly(checklistResponse.getItems().get(1).getChecklistItemId());
    }

    private void editChecklistItem_nullContent(String accessToken, UUID listItemId, UUID checklistItemId) {
        Response response = ChecklistActions.getEditChecklistItemResponse(getServerPort(), accessToken, listItemId, checklistItemId, null);

        ResponseValidator.verifyInvalidParam(response, "content", "must not be null");
    }

    private void editChecklistItem(String accessToken, UUID checklistItemId, UUID listItemId) {
        ChecklistActions.editChecklistItem(getServerPort(), accessToken, listItemId, checklistItemId, CONTENT);

        ChecklistResponse checklistResponse = ChecklistActions.getChecklist(getServerPort(), accessToken, listItemId);

        assertThat(checklistResponse.getItems())
            .hasSize(1)
            .extracting(ChecklistItemModel::getContent).containsExactly(CONTENT);
    }

    private ChecklistItemModel findByOrder(List<ChecklistItemModel> items, int order) {
        return items.stream()
            .filter(checklistItemResponse -> checklistItemResponse.getIndex().equals(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No checklistItem found with order " + 2));
    }
}
