package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
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
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ChecklistControllerImplTestIt_createChecklistItem {
    private static final String CONTENT = "content";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final Integer ORDER = 324;

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private ListItemDao listItemDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ChecklistItemDao checklistItemDao;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
        checklistItemDao.deleteAll();
    }

    @Test
    public void blankTitle() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void parentNotFound() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void parentNotCategory() {
        ListItem listItem = ListItem.builder()
            .userId(USER_ID)
            .listItemId(PARENT)
            .title("t")
            .type(ListItemType.TEXT)
            .build();
        saveListItem(listItem);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void nullNodes() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(null)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("nodes")).isEqualTo("must not be null");
    }

    @Test
    public void nullContent() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.content")).isEqualTo("must not be null");
    }

    @Test
    public void nullChecked() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.checked")).isEqualTo("must not be null");
    }

    @Test
    public void nullOrder() {
        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.order")).isEqualTo("must not be null");
    }

    @Test
    public void createChecklist() {
        ListItem parent = ListItem.builder()
            .userId(USER_ID)
            .listItemId(PARENT)
            .title("t")
            .type(ListItemType.CATEGORY)
            .build();
        saveListItem(parent);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = response.getBody().jsonPath().getUUID("value");

        ListItem listItem = query(() -> listItemDao.findById(listItemId))
            .orElseThrow(() -> new RuntimeException("List item not created"));
        assertThat(listItem.getType()).isEqualTo(ListItemType.CHECKLIST);
        assertThat(listItem.getTitle()).isEqualTo(TITLE);
        assertThat(listItem.getParent()).isEqualTo(PARENT);

        List<ChecklistItem> checklistItems = query(() -> checklistItemDao.getByParent(listItemId));
        assertThat(checklistItems).hasSize(1);
        assertThat(checklistItems.get(0).getChecked()).isTrue();
        assertThat(checklistItems.get(0).getOrder()).isEqualTo(ORDER);

        Content content = query(() -> contentDao.findByParentValidated(checklistItems.get(0).getChecklistItemId()));
        assertThat(content.getContent()).isEqualTo(CONTENT);
    }

    private void saveListItem(ListItem listItem) {
        try {
            accessTokenProvider.set(ACCESS_TOKEN_HEADER);
            listItemDao.save(listItem);
        } finally {
            accessTokenProvider.clear();
        }
    }

    private <T> T query(Supplier<T> supplier) {
        try {
            accessTokenProvider.set(ACCESS_TOKEN_HEADER);
            return supplier.get();
        } finally {
            accessTokenProvider.clear();
        }
    }
}