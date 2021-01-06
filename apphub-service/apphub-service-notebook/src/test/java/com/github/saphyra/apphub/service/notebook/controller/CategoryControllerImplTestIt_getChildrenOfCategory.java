package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.response.ChildrenOfCategoryResponse;
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
import java.util.HashMap;
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
public class CategoryControllerImplTestIt_getChildrenOfCategory {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final UUID LIST_ITEM_ID_1 = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID_2 = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();

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
    public void invalidType() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("type", "asd,fwr");

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY, new HashMap<>(), queryParams));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("type")).isEqualTo("contains invalid argument");
    }

    @Test
    public void getChildrenOfCategory() {
        ListItem parent = ListItem.builder()
            .listItemId(PARENT_ID)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_1)
            .build();
        saveListItem(parent);
        ListItem category = ListItem.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID)
            .type(ListItemType.CATEGORY)
            .title(TITLE_2)
            .parent(PARENT_ID)
            .build();
        saveListItem(category);
        ListItem text = ListItem.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID)
            .type(ListItemType.TEXT)
            .title(TITLE_2)
            .parent(PARENT_ID)
            .build();
        saveListItem(text);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("type", ListItemType.CATEGORY.name());
        queryParams.put("categoryId", PARENT_ID.toString());

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHILDREN_OF_CATEGORY, new HashMap<>(), queryParams));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ChildrenOfCategoryResponse result = response.getBody().as(ChildrenOfCategoryResponse.class);
        assertThat(result.getTitle()).isEqualTo(TITLE_1);
        assertThat(result.getParent()).isNull();
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getId()).isEqualTo(LIST_ITEM_ID_1);
        assertThat(result.getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(result.getChildren().get(0).getType()).isEqualTo(ListItemType.CATEGORY.name());
    }

    private void saveListItem(ListItem listItem) {
        try {
            accessTokenProvider.set(ACCESS_TOKEN_HEADER);
            listItemDao.save(listItem);
        } finally {
            accessTokenProvider.clear();
        }
    }
}