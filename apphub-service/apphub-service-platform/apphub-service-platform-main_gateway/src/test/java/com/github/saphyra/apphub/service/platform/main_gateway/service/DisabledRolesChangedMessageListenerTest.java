package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication.DisabledRoleCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DisabledRolesChangedMessageListenerTest {
    @Mock
    private DisabledRoleCache disabledRoleCache;

    @InjectMocks
    private DisabledRolesChangedMessageListener underTest;

    @Test
    void onMessage() {
        underTest.onMessage(null);

        then(disabledRoleCache).should().clear();
    }
}