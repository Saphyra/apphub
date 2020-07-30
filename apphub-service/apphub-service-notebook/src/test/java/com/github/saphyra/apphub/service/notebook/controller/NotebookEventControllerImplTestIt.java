package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class NotebookEventControllerImplTestIt {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .build();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ListItemDao listItemDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ChecklistItemDao checklistItemDao;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @After
    public void clear() {
        listItemDao.deleteAll();
        contentDao.deleteAll();
    }

    @Test
    public void deleteAccountEvent() {
        ListItem listItem = ListItem.builder()
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

        ChecklistItem checklistItem = ChecklistItem.builder()
            .checklistItemId(UUID.randomUUID())
            .parent(UUID.randomUUID())
            .userId(USER_ID)
            .order(1)
            .checked(true)
            .build();
        save(() -> checklistItemDao.save(checklistItem));

        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();
        Response response = RequestFactory.createRequest()
            .body(eventRequest)
            .post(UrlFactory.create(serverPort, Endpoints.DELETE_ACCOUNT_EVENT));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(query(() -> listItemDao.findAll())).isEmpty();
        assertThat(query(() -> contentDao.findAll())).isEmpty();
        assertThat(query(() -> checklistItemDao.findAll())).isEmpty();
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