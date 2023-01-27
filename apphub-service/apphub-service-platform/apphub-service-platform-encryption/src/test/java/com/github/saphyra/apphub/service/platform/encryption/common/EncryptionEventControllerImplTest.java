package com.github.saphyra.apphub.service.platform.encryption.common;

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
public class EncryptionEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao dao;

    private EncryptionEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new EncryptionEventControllerImpl(List.of(dao));
    }

    @Test
    public void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        underTest.deleteAccountEvent(sendEventRequest);

        verify(dao).deleteByUserId(USER_ID);
    }
}