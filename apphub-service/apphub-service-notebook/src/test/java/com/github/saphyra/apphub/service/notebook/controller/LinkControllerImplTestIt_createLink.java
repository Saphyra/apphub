package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.LinkRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
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
public class LinkControllerImplTestIt_createLink {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final UUID PARENT = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private ListItemDao listItemDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

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
        LinkRequest linkRequest = LinkRequest.builder()
            .title(" ")
            .url(URL)
            .parent(null)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(linkRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void parentNotFound() {
        LinkRequest linkRequest = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(linkRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
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

        LinkRequest linkRequest = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(PARENT)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(linkRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void nullContent() {
        LinkRequest linkRequest = LinkRequest.builder()
            .title(TITLE)
            .url(null)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(linkRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("url")).isEqualTo("must not be null");
    }

    @Test
    public void createLink() {
        ListItem listItem = ListItem.builder()
            .userId(USER_ID)
            .listItemId(PARENT)
            .title("t")
            .type(ListItemType.CATEGORY)
            .build();
        saveListItem(listItem);

        LinkRequest linkRequest = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(PARENT)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(linkRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_LINK));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID textId = response.getBody().jsonPath().getUUID("value");

        ListItem textItem = query(() -> listItemDao.findById(textId))
            .orElseThrow(() -> new RuntimeException("List item not created for new text item"));
        assertThat(textItem.getType()).isEqualTo(ListItemType.LINK);
        assertThat(textItem.getTitle()).isEqualTo(TITLE);
        assertThat(textItem.getParent()).isEqualTo(PARENT);

        Content content = query(() -> contentDao.findByParentValidated(textId));
        assertThat(content.getContent()).isEqualTo(URL);
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