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
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

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
        ChecklistItemModel edit_validNodeRequest = edit_blankTitle(accessTokenId, listItemId, checklistItemId);
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
        CreateChecklistRequest request = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(
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
        listItemId = ChecklistActions.createChecklist(getServerPort(), accessTokenId, request);
        ChecklistResponse checklistResponse = order(accessTokenId, listItemId);
        deleteRow(accessTokenId, listItemId, checklistResponse);
        editChecklistItem_nullContent(accessTokenId, checklistResponse.getItems().get(1).getChecklistItemId());
        editChecklistItem(accessTokenId, checklistResponse.getItems().get(1).getChecklistItemId(), listItemId);
    }

    private static void create_blankTitle(UUID accessTokenId) {
        CreateChecklistRequest create_blankTitleRequest = CreateChecklistRequest.builder()
            .title(" ")
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_blankTitleResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateChecklistRequest create_parentNotFoundRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotFoundResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static UUID create_parentNotCategory(UUID accessTokenId) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessTokenId, CreateTextRequest.builder().title("a").content("").build());
        CreateChecklistRequest create_parentNotCategoryRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotCategoryResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
        return notCategoryParentId;
    }

    private static void create_nullNodes(UUID accessTokenId) {
        CreateChecklistRequest create_nullNodesRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(null)
            .build();
        Response create_nullNodesResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_nullNodesRequest);
        verifyInvalidParam(create_nullNodesResponse, "items", "must not be null");
    }

    private static void create_nullContent(UUID accessTokenId) {
        CreateChecklistRequest create_nullContentRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();
        Response create_nullContentResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_nullContentRequest);
        verifyInvalidParam(create_nullContentResponse, "item.content", "must not be null");
    }

    private static void create_nullChecked(UUID accessTokenId) {
        CreateChecklistRequest create_nullCheckedRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullCheckedResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_nullCheckedRequest);
        verifyInvalidParam(create_nullCheckedResponse, "item.checked", "must not be null");
    }

    private static void create_nullOrder(UUID accessTokenId) {
        CreateChecklistRequest create_nullOrderRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullOrderResponse = ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, create_nullOrderRequest);
        verifyInvalidParam(create_nullOrderResponse, "item.index", "must not be null");
    }

    private BiWrapper<UUID, UUID> create(UUID accessTokenId) {
        CreateChecklistRequest createRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        UUID listItemId = ChecklistActions.createChecklist(getServerPort(), accessTokenId, createRequest);
        ChecklistResponse createdChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        UUID checklistItemId = createdChecklistItemResponse.getItems()
            .get(0)
            .getChecklistItemId();
        assertThat(createdChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(createdChecklistItemResponse.getItems()).hasSize(1);
        assertThat(createdChecklistItemResponse.getItems().get(0).getChecklistItemId()).isNotNull();
        assertThat(createdChecklistItemResponse.getItems().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(createdChecklistItemResponse.getItems().get(0).getChecked()).isTrue();
        assertThat(createdChecklistItemResponse.getItems().get(0).getIndex()).isEqualTo(ORDER);

        return new BiWrapper<>(checklistItemId, listItemId);
    }

    private static void listItemNotFound(UUID accessTokenId) {
        Response get_listItemNotFoundResponse = ChecklistActions.getChecklistResponse(getServerPort(), accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);
    }

    private static ChecklistItemModel edit_blankTitle(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_validNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .type(ItemType.EXISTING)
            .build();
        EditChecklistRequest edit_blankTitleRequest = EditChecklistRequest.builder()
            .title(" ")
            .items(Arrays.asList(edit_validNodeRequest))
            .build();
        Response edit_blankTitleResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_blankTitleRequest, listItemId);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
        return edit_validNodeRequest;
    }

    private static void edit_nullContent(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullContentNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(true)
            .content(null)
            .build();
        EditChecklistRequest edit_nullContentRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_nullContentNodeRequest))
            .build();
        Response edit_nullContentResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_nullContentRequest, listItemId);
        verifyInvalidParam(edit_nullContentResponse, "item.content", "must not be null");
    }

    private static void edit_nullChecked(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullCheckedNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(NEW_ORDER)
            .checked(null)
            .content(NEW_CONTENT)
            .build();
        EditChecklistRequest edit_nullCheckedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_nullCheckedNodeRequest))
            .build();
        Response edit_nullCheckedResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_nullCheckedRequest, listItemId);
        verifyInvalidParam(edit_nullCheckedResponse, "item.checked", "must not be null");
    }

    private static void edit_nullOrder(UUID accessTokenId, UUID listItemId, UUID checklistItemId) {
        ChecklistItemModel edit_nullOrderNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(checklistItemId)
            .index(null)
            .checked(true)
            .content(NEW_CONTENT)
            .build();
        EditChecklistRequest edit_nullOrderRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_nullOrderNodeRequest))
            .build();
        Response edit_nullOrderResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_nullOrderRequest, listItemId);
        verifyInvalidParam(edit_nullOrderResponse, "item.index", "must not be null");
    }

    private static void edit_listItemNotFound(UUID accessTokenId, ChecklistItemModel edit_validNodeRequest) {
        EditChecklistRequest edit_listItemNotFoundRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_validNodeRequest))
            .build();
        Response edit_listItemNotFoundResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(edit_listItemNotFoundResponse);
    }

    private static void edit_checklistItemNotFound(UUID accessTokenId, UUID listItemId) {
        ChecklistItemModel edit_checklistItemNotFoundNodeRequest = ChecklistItemModel.builder()
            .checklistItemId(UUID.randomUUID())
            .index(NEW_ORDER)
            .checked(true)
            .content(NEW_CONTENT)
            .type(ItemType.EXISTING)
            .build();

        EditChecklistRequest edit_checklistItemNotFoundRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_checklistItemNotFoundNodeRequest))
            .build();

        Response edit_checklistItemNotFoundResponse = ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, edit_checklistItemNotFoundRequest, listItemId);
        ResponseValidator.verifyErrorResponse(edit_checklistItemNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_checklistItemDeleted(UUID accessTokenId, UUID listItemId) {
        EditChecklistRequest edit_checklistItemDeletedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Collections.emptyList())
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessTokenId, edit_checklistItemDeletedRequest, listItemId);
        ChecklistResponse deletedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        assertThat(deletedChecklistItemResponse.getItems()).isEmpty();
        assertThat(deletedChecklistItemResponse.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static ChecklistResponse edit_checklistItemAdded(UUID accessTokenId, UUID listItemId) {
        ChecklistItemModel edit_checklistItemAddedNodeRequest = ChecklistItemModel.builder()
            .index(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .type(ItemType.NEW)
            .build();
        EditChecklistRequest edit_checklistItemAddedRequest = EditChecklistRequest.builder()
            .title(NEW_TITLE)
            .items(Arrays.asList(edit_checklistItemAddedNodeRequest))
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessTokenId, edit_checklistItemAddedRequest, listItemId);
        ChecklistResponse checklistItemAddedResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        assertThat(checklistItemAddedResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistItemAddedResponse.getItems()).hasSize(1);
        assertThat(checklistItemAddedResponse.getItems().get(0).getIndex()).isEqualTo(NEW_ORDER);
        assertThat(checklistItemAddedResponse.getItems().get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistItemAddedResponse.getItems().get(0).getChecked()).isFalse();
        return checklistItemAddedResponse;
    }

    private static void check_listItemNotFound(UUID accessTokenId) {
        Response check_listItemNotFoundResponse = ChecklistActions.getUpdateChecklistItemStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), true);
        ResponseValidator.verifyErrorResponse(check_listItemNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void check(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        ChecklistActions.updateChecklistItemStatus(getServerPort(), accessTokenId, checklistItemAddedResponse.getItems().get(0).getChecklistItemId(), true);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId).getItems().get(0).getChecked()).isTrue();
    }

    private static void uncheck(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        ChecklistActions.updateChecklistItemStatus(getServerPort(), accessTokenId, checklistItemAddedResponse.getItems().get(0).getChecklistItemId(), false);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId).getItems().get(0).getChecked()).isFalse();
    }

    private static void edit_checklistItemModified(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistItemAddedResponse) {
        UUID checklistItemId;
        checklistItemId = checklistItemAddedResponse.getItems()
            .get(0)
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
            .items(Arrays.asList(edit_checklistItemModifiedNodeRequest))
            .build();
        ChecklistActions.editChecklist(getServerPort(), accessTokenId, edit_checklistItemModifiedRequest, listItemId);
        ChecklistResponse modifiedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        assertThat(modifiedChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(modifiedChecklistItemResponse.getItems()).hasSize(1);
        assertThat(modifiedChecklistItemResponse.getItems().get(0).getIndex()).isEqualTo(ORDER);
        assertThat(modifiedChecklistItemResponse.getItems().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(modifiedChecklistItemResponse.getItems().get(0).getChecked()).isTrue();
    }

    private static void delete_deleteChecked(UUID accessTokenId, UUID listItemId) {
        Response response = ChecklistActions.getDeleteCheckedChecklistItemsResponse(getServerPort(), accessTokenId, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
        ChecklistResponse deleteCheckedChecklistItemResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        assertThat(deleteCheckedChecklistItemResponse.getItems()).isEmpty();
    }

    private static void deleteChecklist(UUID accessTokenId, UUID notCategoryParentId, UUID listItemId) {
        ListItemActions.deleteListItem(getServerPort(), accessTokenId, listItemId);
        assertThat(CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, notCategoryParentId).getChildren()).isEmpty();
    }

    private ChecklistResponse order(UUID accessTokenId, UUID listItemId) {
        Response orderResponse = ChecklistActions.getOrderItemsResponse(getServerPort(), accessTokenId, listItemId);
        assertThat(orderResponse.getStatusCode()).isEqualTo(200);
        ChecklistResponse checklistResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);
        assertThat(checklistResponse.getItems()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getItems(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getItems(), 1).getContent()).isEqualTo("B");
        return checklistResponse;
    }

    private static void deleteRow(UUID accessTokenId, UUID listItemId, ChecklistResponse checklistResponse) {
        Response response = ChecklistActions.getDeleteChecklistItemResponse(getServerPort(), accessTokenId, checklistResponse.getItems().get(0).getChecklistItemId());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId).getItems())
            .extracting(ChecklistItemModel::getChecklistItemId)
            .containsExactly(checklistResponse.getItems().get(1).getChecklistItemId());
    }

    private void editChecklistItem_nullContent(UUID accessTokenId, UUID checklistItemId) {
        Response response = ChecklistActions.getEditChecklistItemResponse(getServerPort(), accessTokenId, checklistItemId, null);

        ResponseValidator.verifyInvalidParam(response, "content", "must not be null");
    }

    private void editChecklistItem(UUID accessTokenId, UUID checklistItemId, UUID listItemId) {
        ChecklistActions.editChecklistItem(getServerPort(), accessTokenId, checklistItemId, CONTENT);

        ChecklistResponse checklistResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);

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
