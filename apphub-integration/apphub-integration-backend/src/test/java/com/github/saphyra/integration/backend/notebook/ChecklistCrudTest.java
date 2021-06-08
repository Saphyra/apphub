package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditChecklistItemRequest;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.CATEGORY_NOT_FOUND;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_TYPE;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistCrudTest extends BackEndTest {
    private static final Integer ORDER = 234;
    private static final String CONTENT = "content";
    private static final String TITLE = "title";
    private static final Integer NEW_ORDER = 345;
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @Test(dataProvider = "languageDataProvider")
    public void checklistCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Blank title
        CreateChecklistItemRequest create_blankTitleRequest = CreateChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(language, create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateChecklistItemRequest create_parentNotFoundRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyParentNotFound(language, create_parentNotFoundResponse);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title("a").content("").build());
        CreateChecklistItemRequest create_parentNotCategoryRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyInvalidType(language, create_parentNotCategoryResponse);

        //Create - Null nodes
        CreateChecklistItemRequest create_nullNodesRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(null)
            .build();
        Response create_nullNodesResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_nullNodesRequest);
        verifyInvalidParam(language, create_nullNodesResponse, "nodes", "must not be null");

        //Create - Null content
        CreateChecklistItemRequest create_nullContentRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();
        Response create_nullContentResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_nullContentRequest);
        verifyInvalidParam(language, create_nullContentResponse, "node.content", "must not be null");

        //Create - Null checked
        CreateChecklistItemRequest create_nullCheckedRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullCheckedResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_nullCheckedRequest);
        verifyInvalidParam(language, create_nullCheckedResponse, "node.checked", "must not be null");

        //Create - Null order
        CreateChecklistItemRequest create_nullOrderRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        Response create_nullOrderResponse = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, create_nullOrderRequest);
        verifyInvalidParam(language, create_nullOrderResponse, "node.order", "must not be null");

        //Create
        CreateChecklistItemRequest createRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, createRequest);
        ChecklistResponse createdChecklistItemResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(createdChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(createdChecklistItemResponse.getNodes()).hasSize(1);
        assertThat(createdChecklistItemResponse.getNodes().get(0).getChecklistItemId()).isNotNull();
        assertThat(createdChecklistItemResponse.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(createdChecklistItemResponse.getNodes().get(0).getChecked()).isTrue();
        assertThat(createdChecklistItemResponse.getNodes().get(0).getOrder()).isEqualTo(ORDER);

        //Get - ListItem not found
        Response get_listItemNotFoundResponse = NotebookActions.getChecklistResponse(language, accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(language, get_listItemNotFoundResponse);

        //Edit - Blank title
        UUID checklistItemId = createdChecklistItemResponse.getNodes()
            .get(0)
            .getChecklistItemId();

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
        Response edit_blankTitleResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_blankTitleRequest, listItemId);
        verifyInvalidParam(language, edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Null content
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
        Response edit_nullContentResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_nullContentRequest, listItemId);
        verifyInvalidParam(language, edit_nullContentResponse, "node.content", "must not be null");

        //Edit - Null checked
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
        Response edit_nullCheckedResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_nullCheckedRequest, listItemId);
        verifyInvalidParam(language, edit_nullCheckedResponse, "node.checked", "must not be null");

        //Edit - Null order
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
        Response edit_nullOrderResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_nullOrderRequest, listItemId);
        verifyInvalidParam(language, edit_nullOrderResponse, "node.order", "must not be null");

        //Edit - List item not found
        EditChecklistItemRequest edit_listItemNotFoundRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_validNodeRequest))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(language, edit_listItemNotFoundResponse);

        //Edit - Checklist item not found
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

        Response edit_checklistItemNotFoundResponse = NotebookActions.getEditChecklistResponse(language, accessTokenId, edit_checklistItemNotFoundRequest, listItemId);
        verifyListItemNotFound(language, edit_checklistItemNotFoundResponse);

        //Edit - ChecklistItem deleted
        EditChecklistItemRequest edit_checklistItemDeletedRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Collections.emptyList())
            .build();
        NotebookActions.editChecklistItem(language, accessTokenId, edit_checklistItemDeletedRequest, listItemId);
        ChecklistResponse deletedChecklistItemResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(deletedChecklistItemResponse.getNodes()).isEmpty();
        assertThat(deletedChecklistItemResponse.getTitle()).isEqualTo(NEW_TITLE);

        //Edit - ChecklistItem added
        ChecklistItemNodeRequest edit_checklistItemAddedNodeRequest = ChecklistItemNodeRequest.builder()
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build();
        EditChecklistItemRequest edit_checklistItemAddedRequest = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Arrays.asList(edit_checklistItemAddedNodeRequest))
            .build();
        NotebookActions.editChecklistItem(language, accessTokenId, edit_checklistItemAddedRequest, listItemId);
        ChecklistResponse checklistItemAddedResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(checklistItemAddedResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistItemAddedResponse.getNodes()).hasSize(1);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getOrder()).isEqualTo(NEW_ORDER);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(checklistItemAddedResponse.getNodes().get(0).getChecked()).isFalse();

        //Check - listItemNotFound
        Response check_listItemNotFoundResponse = NotebookActions.getUpdateChecklistItemStatusResponse(language, accessTokenId, UUID.randomUUID(), true);
        verifyListItemNotFound(language, check_listItemNotFoundResponse);

        //Check
        NotebookActions.updateChecklistItemStatus(language, accessTokenId, checklistItemAddedResponse.getNodes().get(0).getChecklistItemId(), true);
        assertThat(NotebookActions.getChecklist(language, accessTokenId, listItemId).getNodes().get(0).getChecked()).isTrue();

        //Uncheck
        NotebookActions.updateChecklistItemStatus(language, accessTokenId, checklistItemAddedResponse.getNodes().get(0).getChecklistItemId(), false);
        assertThat(NotebookActions.getChecklist(language, accessTokenId, listItemId).getNodes().get(0).getChecked()).isFalse();

        //Edit - ChecklistItem modified
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
        NotebookActions.editChecklistItem(language, accessTokenId, edit_checklistItemModifiedRequest, listItemId);
        ChecklistResponse modifiedChecklistItemResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(modifiedChecklistItemResponse.getTitle()).isEqualTo(TITLE);
        assertThat(modifiedChecklistItemResponse.getNodes()).hasSize(1);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getOrder()).isEqualTo(ORDER);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(modifiedChecklistItemResponse.getNodes().get(0).getChecked()).isTrue();

        //Delete - Delete checked
        Response response = NotebookActions.getDeleteCheckedChecklistItemsResponse(language, accessTokenId, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
        ChecklistResponse deleteCheckedChecklistItemResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(deleteCheckedChecklistItemResponse.getNodes()).isEmpty();

        //Delete - Delete checklist
        NotebookActions.deleteListItem(language, accessTokenId, listItemId);
        assertThat(NotebookActions.getChildrenOfCategory(language, accessTokenId, notCategoryParentId).getChildren()).isEmpty();

        //Order
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
        listItemId = NotebookActions.createChecklist(language, accessTokenId, request);
        Response orderResponse = NotebookActions.orderChecklistItems(language, accessTokenId, listItemId);
        assertThat(orderResponse.getStatusCode()).isEqualTo(200);
        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);
        assertThat(checklistResponse.getNodes()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getNodes(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getNodes(), 1).getContent()).isEqualTo("B");
    }

    private void verifyListItemNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    private void verifyInvalidType(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    private void verifyParentNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, CATEGORY_NOT_FOUND));
    }

    private void verifyInvalidParam(Language language, Response response, String field, String value) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    private ChecklistItemResponse findByOrder(List<ChecklistItemResponse> nodes, int order) {
        return nodes.stream()
            .filter(checklistItemResponse -> checklistItemResponse.getOrder().equals(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No checklistItem found with order " + 2));
    }
}
