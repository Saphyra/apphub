package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.File;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHeadDao;
import com.github.saphyra.apphub.test.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotebookEventControllerImplItTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessToken ACCESS_TOKEN_HEADER = AccessToken.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .build();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private DeprecatedListItemDao listItemDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private TableHeadDao tableHeadDao;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private CheckedItemDao checkedItemDao;

    @Autowired
    private ColumnTypeDao columnTypeDao;

    @Autowired
    private DimensionDao dimensionDao;

    @Autowired
    private FileDao fileDao;

    @AfterEach
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
        tableHeadDao.deleteAll();
        checkedItemDao.deleteAll();
        columnTypeDao.deleteAll();
        dimensionDao.deleteAll();
        fileDao.deleteAll();
    }

    @Test
    public void deleteAccountEvent() {
        DeprecatedListItem listItem = DeprecatedListItem.builder()
            .listItemId(UUID.randomUUID())
            .title(TITLE)
            .type(ListItemType.TEXT)
            .userId(USER_ID)
            .build();
        save(() -> listItemDao.save(listItem));

        Content content = Content.builder()
            .contentId(UUID.randomUUID())
            .userId(USER_ID)
            .parent(UUID.randomUUID())
            .content(CONTENT)
            .build();
        save(() -> contentDao.save(content));

        TableHead tableHead = TableHead.builder()
            .tableHeadId(UUID.randomUUID())
            .parent(UUID.randomUUID())
            .userId(USER_ID)
            .columnIndex(32)
            .build();
        save(() -> tableHeadDao.save(tableHead));

        CheckedItem checkedItem = CheckedItem.builder()
            .checkedItemId(UUID.randomUUID())
            .userId(USER_ID)
            .checked(true)
            .build();
        save(() -> checkedItemDao.save(checkedItem));

        ColumnTypeDto columnType = ColumnTypeDto.builder()
            .columnId(UUID.randomUUID())
            .userId(USER_ID)
            .type(ColumnType.LINK)
            .build();
        save(() -> columnTypeDao.save(columnType));

        Dimension dimension = Dimension.builder()
            .dimensionId(UUID.randomUUID())
            .userId(USER_ID)
            .externalReference(UUID.randomUUID())
            .index(32)
            .build();
        save(() -> dimensionDao.save(dimension));

        File file = File.builder()
            .fileId(UUID.randomUUID())
            .userId(USER_ID)
            .build();
        save(() -> fileDao.save(file));

        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();
        Response response = RequestFactory.createRequest()
            .body(eventRequest)
            .post(UrlFactory.create(serverPort, GenericEndpoints.EVENT_DELETE_ACCOUNT));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(query(() -> listItemDao.findAll())).isEmpty();
        assertThat(query(() -> contentDao.findAll())).isEmpty();
        assertThat(query(() -> tableHeadDao.findAll())).isEmpty();
        assertThat(query(() -> checkedItemDao.findAll())).isEmpty();
        assertThat(query(() -> columnTypeDao.findAll())).isEmpty();
        assertThat(query(() -> dimensionDao.findAll())).isEmpty();
        assertThat(query(() -> fileDao.findAll())).isEmpty();
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