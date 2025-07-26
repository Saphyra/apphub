package com.github.saphyra.apphub.service.calendar_deprecated.service;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CalendarEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao dao;

    private CalendarEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CalendarEventControllerImpl(List.of(dao));
    }

    @Test
    public void deleteAccountEvent() {
        underTest.deleteAccountEvent(new SendEventRequest<>(null, null, new DeleteAccountEvent(USER_ID)));

        verify(dao).deleteByUserId(USER_ID);
    }
}