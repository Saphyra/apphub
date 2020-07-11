package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.api.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
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
import java.util.Optional;
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
public class CategoryControllerImplTestIt_createCategory {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE_1 = "title-1";
    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final String TITLE_2 = "title-2";

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

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        listItemDao.deleteAll();
    }

    @Test
    public void blankTitle() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(" ").build())
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CATEGORY));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void parentNotFound() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(TITLE_1).parent(PARENT_ID).build())
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CATEGORY));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void parentIsNotCategory() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CHECKLIST)
            .title(TITLE_2)
            .build();
        saveListItem(listItem);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(TITLE_1).parent(PARENT_ID).build())
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CATEGORY));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void successfullyCreated() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_2)
            .build();
        saveListItem(listItem);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(TITLE_1).parent(PARENT_ID).build())
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CATEGORY));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        String categoryIdString = response.getBody().jsonPath().getString("value");
        UUID categoryId = UUID.fromString(categoryIdString);
        Optional<ListItem> listItemOptional = query(() -> listItemDao.findById(categoryId));
        assertThat(listItemOptional).isPresent();
        assertThat(listItemOptional.get().getParent()).isEqualTo(PARENT_ID);
        assertThat(listItemOptional.get().getTitle()).isEqualTo(TITLE_1);
        assertThat(listItemOptional.get().getType()).isEqualTo(ListItemType.CATEGORY);
    }

    @Test
    public void successfullyCreated_noParent() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(CreateCategoryRequest.builder().title(TITLE_1).parent(null).build())
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CATEGORY));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        String categoryIdString = response.getBody().jsonPath().getString("value");
        UUID categoryId = UUID.fromString(categoryIdString);
        Optional<ListItem> listItemOptional = query(() -> listItemDao.findById(categoryId));
        assertThat(listItemOptional).isPresent();
        assertThat(listItemOptional.get().getParent()).isNull();
        assertThat(listItemOptional.get().getTitle()).isEqualTo(TITLE_1);
        assertThat(listItemOptional.get().getType()).isEqualTo(ListItemType.CATEGORY);
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