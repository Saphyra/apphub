package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistItemResponse;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ChecklistControllerImplTestIt_orderItems {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE = "title";

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
    public void orderItems() {
        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(
                ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content("B")
                    .build(),
                ChecklistItemNodeRequest.builder()
                    .order(1)
                    .checked(false)
                    .content("A")
                    .build())
            )
            .build();
        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(createChecklistItemRequest)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        ChecklistResponse checklistResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHECKLIST_ITEM, "listItemId", listItemId))
            .getBody()
            .as(ChecklistResponse.class);

        assertThat(checklistResponse.getNodes()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getNodes(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getNodes(), 1).getContent()).isEqualTo("B");
    }

    private ChecklistItemResponse findByOrder(List<ChecklistItemResponse> nodes, int order) {
        return nodes.stream()
            .filter(checklistItemResponse -> checklistItemResponse.getOrder().equals(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No ChecklistItem found with order " + order));
    }
}