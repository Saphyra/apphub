package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.notebook.service.custom_table_deprecated.CustomTableDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotebookEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CustomTableDeletionService customTableDeletionService;

    @Mock
    private DeleteByUserIdDao dao;

    private NotebookEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new NotebookEventControllerImpl(customTableDeletionService, Arrays.asList(dao));
    }

    @Test
    public void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> eventRequest = SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build();

        underTest.deleteAccountEvent(eventRequest);

        verify(customTableDeletionService).deleteForUser(USER_ID);
        verify(dao).deleteByUserId(USER_ID);
    }
}