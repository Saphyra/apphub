package com.github.saphyra.apphub.service.custom.villany_atesz;

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

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class VillanyAteszEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao deleteByUserIdDao;

    private VillanyAteszEventControllerImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new VillanyAteszEventControllerImpl(List.of(deleteByUserIdDao));
    }

    @Test
    void deleteAccountEvent() {
        underTest.deleteAccountEvent(SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build());

        then(deleteByUserIdDao).should().deleteByUserId(USER_ID);
    }
}