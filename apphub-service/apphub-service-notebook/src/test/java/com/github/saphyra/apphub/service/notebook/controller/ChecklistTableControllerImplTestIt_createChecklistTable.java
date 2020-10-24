package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
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
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
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
public class ChecklistTableControllerImplTestIt_createChecklistTable {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

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
    public void blankTitle() {
        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(" ")
            .parent(null)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void parentNotFound() {
        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void parentNotCategory() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT)
            .userId(USER_ID)
            .title(TITLE)
            .type(ListItemType.TABLE)
            .build();
        save(() -> listItemDao.save(listItem));

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void blankColumnName() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT)
            .userId(USER_ID)
            .title(TITLE)
            .type(ListItemType.CATEGORY)
            .build();
        save(() -> listItemDao.save(listItem));

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(" "))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columnName")).isEqualTo("must not be null or blank");
    }

    @Test
    public void differentColumnCount() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT)
            .userId(USER_ID)
            .title(TITLE)
            .type(ListItemType.CATEGORY)
            .build();
        save(() -> listItemDao.save(listItem));

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columns")).isEqualTo("amount different");
    }

    @Test
    public void nullContent() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT)
            .userId(USER_ID)
            .title(TITLE)
            .type(ListItemType.CATEGORY)
            .build();
        save(() -> listItemDao.save(listItem));

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME, COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE, null)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columnValue")).isEqualTo("must not be null");
    }

    @Test
    public void createChecklistTable() {
        ListItem listItem = ListItem.builder()
            .listItemId(PARENT)
            .userId(USER_ID)
            .title(TITLE)
            .type(ListItemType.CATEGORY)
            .build();
        save(() -> listItemDao.save(listItem));

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(PARENT)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.CREATE_NOTEBOOK_CHECKLIST_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        UUID listItemId = response.getBody().jsonPath().getUUID("value");

        List<TableHead> tableHeads = query(() -> tableHeadDao.getByParent(listItemId));
        assertThat(tableHeads).hasSize(1);
        assertThat(tableHeads.get(0).getUserId()).isEqualTo(USER_ID);
        assertThat(tableHeads.get(0).getColumnIndex()).isEqualTo(0);

        List<TableJoin> tableJoins = query(() -> tableJoinDao.getByParent(listItemId));
        assertThat(tableJoins).hasSize(1);
        assertThat(tableJoins.get(0).getUserId()).isEqualTo(USER_ID);
        assertThat(tableJoins.get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableJoins.get(0).getRowIndex()).isEqualTo(0);

        Content content = query(() -> contentDao.findByParentValidated(tableJoins.get(0).getTableJoinId()));

        assertThat(content.getUserId()).isEqualTo(USER_ID);
        assertThat(content.getContent()).isEqualTo(COLUMN_VALUE);

        List<ChecklistTableRow> checklistTableRows = query(() -> checklistTableRowDao.getByParent(listItemId));
        assertThat(checklistTableRows).hasSize(1);
        assertThat(checklistTableRows.get(0).getUserId()).isEqualTo(USER_ID);
        assertThat(checklistTableRows.get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableRows.get(0).isChecked()).isTrue();
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