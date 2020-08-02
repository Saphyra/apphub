package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
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
public class ListItemControllerIImplTestIt_editListItem {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_VALUE = "new-value";
    private static final String ORIGINAL_CONTENT = "original-content";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private ListItemDao listItemDao;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private ContentDao contentDao;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
    }

    @Test
    public void blankTitle() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(ORIGINAL_TITLE)
            .parent(PARENT_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(" ")
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void parentNotFound() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(ORIGINAL_TITLE)
            .parent(PARENT_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(UUID.randomUUID())
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void parentNotCategory() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(ORIGINAL_TITLE)
            .parent(PARENT_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.TEXT)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(PARENT_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void listItemNotFound() {
        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void editLink() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.LINK)
            .title(ORIGINAL_TITLE)
            .parent(PARENT_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent));

        ListItem parent2 = ListItem.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent2));

        Content content = Content.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(LIST_ITEM_ID_1)
            .content(ORIGINAL_CONTENT)
            .build();
        save(() -> contentDao.save(content));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(LIST_ITEM_ID_2)
            .value(NEW_VALUE)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getParent()).isEqualTo(LIST_ITEM_ID_2);
        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getTitle()).isEqualTo(NEW_TITLE);
        assertThat(query(() -> contentDao.findByParentValidated(LIST_ITEM_ID_1)).getContent()).isEqualTo(NEW_VALUE);
    }

    @Test
    public void editCategory_ownChild() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(ORIGINAL_TITLE)
            .parent(PARENT_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent));

        ListItem child = ListItem.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(LIST_ITEM_ID_1)
            .build();
        save(() -> listItemDao.save(child));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(LIST_ITEM_ID_2)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("parent")).isEqualTo("must not be own child");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void editCategory() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(ORIGINAL_TITLE)
            .parent(null)
            .build();
        save(() -> listItemDao.save(listItem));

        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(PARENT_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getParent()).isEqualTo(PARENT_ID);
        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getTitle()).isEqualTo(NEW_TITLE);
    }

    @Test
    public void editListItem() {
        ListItem listItem = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(ORIGINAL_TITLE)
            .parent(null)
            .build();
        save(() -> listItemDao.save(listItem));

        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title("af")
            .parent(null)
            .build();
        save(() -> listItemDao.save(parent));

        EditListItemRequest request = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(PARENT_ID)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.EDIT_NOTEBOOK_LIST_ITEM, "listItemId", LIST_ITEM_ID_1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getParent()).isEqualTo(PARENT_ID);
        assertThat(query(() -> listItemDao.findByIdValidated(LIST_ITEM_ID_1)).getTitle()).isEqualTo(NEW_TITLE);
    }

    private void save(Runnable runnable) {
        try {
            accessTokenProvider.set(ACCESS_TOKEN_HEADER);
            runnable.run();
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