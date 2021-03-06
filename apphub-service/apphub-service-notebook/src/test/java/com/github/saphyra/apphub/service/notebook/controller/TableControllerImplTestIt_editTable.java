package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class TableControllerImplTestIt_editTable {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("NOTEBOOK"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ORIGINAL_COLUMN_NAME = "original-column-name";
    private static final String ORIGINAL_COLUMN_VALUE = "original-column-value";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";
    private static final String ORIGINAL_TITLE = "original-title";

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

    private UUID listItemId;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);

        CreateTableRequest request = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .parent(null)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        listItemId = response.getBody().jsonPath().getUUID("value");
    }

    @After
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
        tableHeadDao.deleteAll();
        tableJoinDao.deleteAll();
    }

    @Test
    public void blankTitle() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(new KeyValuePair<>(
                getTableJoinIds(listItemId).get(0),
                NEW_COLUMN_VALUE
            ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test
    public void blankColumnName() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                " "
            )))
            .columns(Arrays.asList(Arrays.asList(new KeyValuePair<>(
                getTableJoinIds(listItemId).get(0),
                NEW_COLUMN_VALUE
            ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columnName")).isEqualTo("must not be null or blank");
    }

    @Test
    public void differentColumnAmount() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    NEW_COLUMN_VALUE
                ), new KeyValuePair<>())))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columns")).isEqualTo("amount different");
    }

    @Test
    public void nullContent() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    null
                ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("columnValue")).isEqualTo("must not be null");
    }

    @Test
    public void tableHeadDoesNotExist() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                UUID.randomUUID(),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    NEW_COLUMN_VALUE
                ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void tableJoinDoesNotExist() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    UUID.randomUUID(),
                    NEW_COLUMN_VALUE
                ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void listItemNotFound() {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    NEW_COLUMN_VALUE
                ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", UUID.randomUUID()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void columnDeleted() {
        UUID tableJoinId = getTableJoinIds(listItemId).get(0);
        UUID tableHeadId = getTableHeadIds(listItemId).get(0);
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .columns(Collections.emptyList())
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> tableHeadDao.getByParent(listItemId))).isEmpty();
        assertThat(query(() -> tableJoinDao.getByParent(listItemId))).isEmpty();
        assertThat(query(() -> contentDao.findByParent(tableJoinId))).isEmpty();
        assertThat(query(() -> contentDao.findByParent(tableHeadId))).isEmpty();
    }

    @Test
    public void columnNameAndValueEdited() {
        UUID tableJoinId = getTableJoinIds(listItemId).get(0);
        UUID tableHeadId = getTableHeadIds(listItemId).get(0);
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(new KeyValuePair<>(
                getTableHeadIds(listItemId).get(0),
                NEW_COLUMN_NAME
            )))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    NEW_COLUMN_VALUE
                ))))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> contentDao.findByParentValidated(tableHeadId)).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(query(() -> contentDao.findByParentValidated(tableJoinId)).getContent()).isEqualTo(NEW_COLUMN_VALUE);
    }

    @Test
    public void columnCreated() {
        UUID tableJoinId = getTableJoinIds(listItemId).get(0);
        UUID tableHeadId = getTableHeadIds(listItemId).get(0);
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    getTableHeadIds(listItemId).get(0),
                    ORIGINAL_COLUMN_NAME
                ),
                new KeyValuePair<>(
                    null,
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    getTableJoinIds(listItemId).get(0),
                    ORIGINAL_COLUMN_VALUE
                ),
                new KeyValuePair<>(
                    null,
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(query(() -> contentDao.findByParentValidated(tableHeadId)).getContent()).isEqualTo(ORIGINAL_COLUMN_NAME);
        assertThat(query(() -> contentDao.findByParentValidated(tableJoinId)).getContent()).isEqualTo(ORIGINAL_COLUMN_VALUE);

        TableHead newTableHead = query(() -> tableHeadDao.getByParent(listItemId))
            .stream()
            .filter(tableHead -> !tableHead.getTableHeadId().equals(tableHeadId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Additional tableHead was not created."));
        assertThat(newTableHead.getColumnIndex()).isEqualTo(1);
        assertThat(query(() -> contentDao.findByParentValidated(newTableHead.getTableHeadId())).getContent()).isEqualTo(NEW_COLUMN_NAME);

        TableJoin newTableJoin = query(() -> tableJoinDao.getByParent(listItemId))
            .stream()
            .filter(tableHead -> !tableHead.getTableJoinId().equals(tableJoinId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Additional tableJoin was not created."));
        assertThat(newTableJoin.getColumnIndex()).isEqualTo(1);
        assertThat(newTableJoin.getRowIndex()).isEqualTo(0);
        assertThat(query(() -> contentDao.findByParentValidated(newTableJoin.getTableJoinId())).getContent()).isEqualTo(NEW_COLUMN_VALUE);
    }

    private List<UUID> getTableHeadIds(UUID listItemId) {
        return query(() -> tableHeadDao.getByParent(listItemId))
            .stream()
            .map(TableHead::getTableHeadId)
            .collect(Collectors.toList());
    }

    private List<UUID> getTableJoinIds(UUID listItemId) {
        return query(() -> tableJoinDao.getByParent(listItemId))
            .stream()
            .map(TableJoin::getTableJoinId)
            .collect(Collectors.toList());
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