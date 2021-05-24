package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ListItemControllerIImplTestIt_clone {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ROOT_TITLE = "root-title";
    private static final String PARENT_TITLE = "parent-title";
    private static final String CHILD_CATEGORY_TITLE = "child-category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = "link-url";
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_ITEM_CONTENT = "checklist-item-content";
    private static final String TABLE_COLUMN_NAME = "table-column-name";
    private static final String TABLE_COLUMN_VALUE = "table-column-value";
    private static final String TEXT_TITLE = "text-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String TABLE_TITLE = "table-title";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private List<AbstractDao<?, ?, ?, ?>> daos;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        daos.forEach(AbstractDao::deleteAll);
    }

    @Test
    public void listItemNotFound() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CLONE_LIST_ITEM, "listItemId", UUID.randomUUID()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void cloneTest() {
        UUID rootId = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(ROOT_TITLE).build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CATEGORY))
            .getBody()
            .jsonPath()
            .getUUID("value");

        UUID parentId = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(PARENT_TITLE).parent(rootId).build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CATEGORY))
            .getBody()
            .jsonPath()
            .getUUID("value");

        UUID childCategoryId = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(CHILD_CATEGORY_TITLE).parent(parentId).build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CATEGORY))
            .getBody()
            .jsonPath()
            .getUUID("value");

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(LinkRequest.builder().title(LINK_TITLE).parent(childCategoryId).url(LINK_URL).build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateTextRequest.builder().title(TEXT_TITLE).parent(parentId).content(TEXT_CONTENT).build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TEXT));

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateChecklistItemRequest.builder()
                .title(CHECKLIST_TITLE)
                .parent(parentId)
                .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content(CHECKLIST_ITEM_CONTENT)
                    .build()))
                .build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .parent(parentId)
                .columnNames(Arrays.asList(TABLE_COLUMN_NAME))
                .columns(Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)))
                .build())
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TABLE));

        Response cloneResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CLONE_LIST_ITEM, "listItemId", parentId));

        assertThat(cloneResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ChildrenOfCategoryResponse rootItems = getChildrenOfCategory(rootId);
        assertThat(rootItems.getChildren()).hasSize(2);
        assertThat(rootItems.getChildren().stream().allMatch(notebookView -> notebookView.getTitle().equals(PARENT_TITLE))).isTrue();

        UUID clonedParentId = rootItems.getChildren()
            .stream()
            .filter(notebookView -> !notebookView.getId().equals(parentId))
            .findFirst()
            .map(NotebookView::getId)
            .orElseThrow(() -> new RuntimeException("Clone not found"));

        ChildrenOfCategoryResponse clonedParentItems = getChildrenOfCategory(clonedParentId);
        assertThat(clonedParentItems.getChildren()).hasSize(4);

        UUID clonedChildCategoryId = findByTitle(CHILD_CATEGORY_TITLE, clonedParentItems.getChildren()).getId();
        ChildrenOfCategoryResponse clonedChildCategoryItems = getChildrenOfCategory(clonedChildCategoryId);
        assertThat(clonedChildCategoryItems.getChildren()).hasSize(1);

        NotebookView linkItem = findByTitle(LINK_TITLE, clonedChildCategoryItems.getChildren());
        assertThat(linkItem.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(linkItem.getValue()).isEqualTo(LINK_URL);

        NotebookView textItem = findByTitle(TEXT_TITLE, clonedParentItems.getChildren());
        assertThat(textItem.getType()).isEqualTo(ListItemType.TEXT.name());
        String textContent = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_TEXT, "listItemId", textItem.getId()))
            .getBody()
            .jsonPath()
            .getString("content");
        assertThat(textContent).isEqualTo(TEXT_CONTENT);

        NotebookView checklistItem = findByTitle(CHECKLIST_TITLE, clonedParentItems.getChildren());
        assertThat(checklistItem.getType()).isEqualTo(ListItemType.CHECKLIST.name());
        ChecklistResponse checklistData = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHECKLIST_ITEM, "listItemId", checklistItem.getId()))
            .getBody()
            .as(ChecklistResponse.class);
        assertThat(checklistData.getNodes()).hasSize(1);
        assertThat(checklistData.getNodes().get(0).getOrder()).isEqualTo(0);
        assertThat(checklistData.getNodes().get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        assertThat(checklistData.getNodes().get(0).getChecked()).isTrue();

        NotebookView tableItem = findByTitle(TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(tableItem.getType()).isEqualTo(ListItemType.TABLE.name());
        TableResponse tableData = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_TABLE, "listItemId", tableItem.getId()))
            .getBody()
            .as(TableResponse.class);
        assertThat(tableData.getTableHeads()).hasSize(1);
        assertThat(tableData.getTableHeads().get(0).getContent()).isEqualTo(TABLE_COLUMN_NAME);
        assertThat(tableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns()).hasSize(1);
        assertThat(tableData.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getContent()).isEqualTo(TABLE_COLUMN_VALUE);
    }

    private ChildrenOfCategoryResponse getChildrenOfCategory(UUID listItemId) {
        Map<String, String> queryParams = new HashMap<String, String>() {{
            put("categoryId", listItemId.toString());
        }};
        return RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY, new HashMap<>(), queryParams))
            .getBody()
            .as(ChildrenOfCategoryResponse.class);
    }

    private NotebookView findByTitle(String title, List<NotebookView> views) {
        return views.stream()
            .filter(notebookView -> notebookView.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("NotebookView not found with title " + title));
    }
}