package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class TextControllerImplTestIt_editText {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private ListItemDao listItemDao;

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
        CreateTextRequest createRequest = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();

        Response saveResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TEXT));
        assertThat(saveResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        UUID textId = saveResponse.getBody().jsonPath().getUUID("value");

        EditTextRequest editRequest = EditTextRequest.builder()
            .title(" ")
            .content(NEW_CONTENT)
            .build();

        Response editResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = editResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void nullContent() {
        CreateTextRequest createRequest = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();

        Response saveResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TEXT));
        assertThat(saveResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        UUID textId = saveResponse.getBody().jsonPath().getUUID("value");

        EditTextRequest editRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(null)
            .build();

        Response editResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = editResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("content")).isEqualTo("must not be null");
    }

    @Test
    public void listItemNotFound() {
        EditTextRequest editRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TEXT, "listItemId", UUID.randomUUID()));

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void editText() {
        CreateTextRequest createRequest = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();

        Response saveResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TEXT));
        assertThat(saveResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        UUID textId = saveResponse.getBody().jsonPath().getUUID("value");

        EditTextRequest editRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .build();

        Response editResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TEXT, "listItemId", textId));

        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        Response queryResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_TEXT, "listItemId", textId));

        TextResponse textResponse = queryResponse.getBody().as(TextResponse.class);
        assertThat(textResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(textResponse.getContent()).isEqualTo(NEW_CONTENT);
    }
}