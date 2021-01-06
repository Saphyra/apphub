package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
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
public class ChecklistTableControllerImplTestIt_deleteCheckedItems {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String CHECKED_CONTENT = "checked-content";
    private static final String UNCHECKED_CONTENT = "unchecked-content";

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
    private TableHeadDao tableHeadDao;

    @Autowired
    private TableJoinDao tableJoinDao;

    @Autowired
    private ChecklistTableRowDao checklistTableRowDao;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
        tableHeadDao.deleteAll();
        tableJoinDao.deleteAll();
        checklistTableRowDao.deleteAll();
    }

    @Test
    public void deleteCheckedItems() {
        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(
                ChecklistTableRowRequest.<String>builder()
                    .checked(true)
                    .columns(Arrays.asList(CHECKED_CONTENT))
                    .build(),
                ChecklistTableRowRequest.<String>builder()
                    .checked(false)
                    .columns(Arrays.asList(UNCHECKED_CONTENT))
                    .build()
            ))
            .build();

        Response createResponse = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_TABLE));

        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        UUID listItemId = createResponse.getBody().jsonPath().getUUID("value");

        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE, "listItemId", listItemId))
            .then()
            .statusCode(HttpStatus.OK.value());

        ChecklistTableResponse response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHECKLIST_TABLE, "listItemId", listItemId))
            .getBody()
            .as(ChecklistTableResponse.class);

        assertThat(response.getTableColumns()).hasSize(1);
        assertThat(response.getTableColumns().get(0).getContent()).isEqualTo(UNCHECKED_CONTENT);
    }
}