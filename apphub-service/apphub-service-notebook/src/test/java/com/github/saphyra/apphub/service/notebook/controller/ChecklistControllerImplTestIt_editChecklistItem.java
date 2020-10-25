package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
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
import java.util.Collections;
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
public class ChecklistControllerImplTestIt_editChecklistItem {
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ORIGINAL_TITLE = "original-title";
    private static final Integer ORIGINAL_ORDER = 324;
    private static final String NEW_CONTENT = "new-content";
    private static final Integer NEW_ORDER = 234;
    private static final String NEW_TITLE = "new-title";

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
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();

        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(false)
            .content(null)
            .build());

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(" ")
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void nullContent() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();

        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(false)
            .content(null)
            .build());

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.content")).isEqualTo("must not be null");
    }

    @Test
    public void nullChecked() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();


        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(null)
            .content(NEW_CONTENT)
            .build());

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.checked")).isEqualTo("must not be null");
    }

    @Test
    public void nullOrder() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();


        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(null)
            .checked(false)
            .content(NEW_CONTENT)
            .build());

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("node.order")).isEqualTo("must not be null");
    }

    @Test
    public void listItemNotFound() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();


        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build());
        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", UUID.randomUUID()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void checklistItemNotFound() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");

        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(UUID.randomUUID())
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build());
        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void checklistItemDeleted() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");

        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(Collections.emptyList())
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> listItemDao.findByIdValidated(listItemId)).getTitle()).isEqualTo(NEW_TITLE);
        assertThat(query(() -> checklistItemDao.findAll())).isEmpty();
        assertThat(query(() -> contentDao.findAll())).isEmpty();
    }

    @Test
    public void checklistItemAdded() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();

        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(null)
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build());
        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ChecklistItem newItem = query(() -> checklistItemDao.findAll())
            .stream()
            .filter(checklistItem -> !checklistItem.getChecklistItemId().equals(checklistItemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("New checklistItem was not created"));

        assertThat(query(() -> listItemDao.findByIdValidated(listItemId)).getTitle()).isEqualTo(NEW_TITLE);
        assertThat(newItem.getOrder()).isEqualTo(NEW_ORDER);
        assertThat(newItem.getChecked()).isFalse();

        assertThat(query(() -> contentDao.findByParentValidated(newItem.getChecklistItemId())).getContent()).isEqualTo(NEW_CONTENT);
    }

    @Test
    public void checklistItemModified() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(ORIGINAL_TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORIGINAL_ORDER)
                .checked(true)
                .content(ORIGINAL_CONTENT)
                .build()))
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");
        UUID checklistItemId = query(() -> checklistItemDao.getByParent(listItemId))
            .get(0)
            .getChecklistItemId();

        List<ChecklistItemNodeRequest> nodes = Arrays.asList(ChecklistItemNodeRequest.builder()
            .checklistItemId(checklistItemId)
            .order(NEW_ORDER)
            .checked(false)
            .content(NEW_CONTENT)
            .build());
        EditChecklistItemRequest request = EditChecklistItemRequest.builder()
            .title(NEW_TITLE)
            .nodes(nodes)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_CHECKLIST_ITEM, "listItemId", listItemId));

        assertThat(query(() -> listItemDao.findByIdValidated(listItemId)).getTitle()).isEqualTo(NEW_TITLE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ChecklistItem editedItem = query(() -> checklistItemDao.findById(checklistItemId.toString())).get();

        assertThat(editedItem.getOrder()).isEqualTo(NEW_ORDER);
        assertThat(editedItem.getChecked()).isFalse();

        assertThat(query(() -> contentDao.findByParentValidated(checklistItemId)).getContent()).isEqualTo(NEW_CONTENT);
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