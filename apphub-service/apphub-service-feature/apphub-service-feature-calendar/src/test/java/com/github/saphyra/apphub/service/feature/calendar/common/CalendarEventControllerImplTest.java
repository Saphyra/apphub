package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CalendarEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao dao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private CalendarEventControllerImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CalendarEventControllerImpl(List.of(dao), accessTokenProvider);
    }

    @Test
    void deleteAccountEvent() throws Exception {
        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();
        given(accessTokenProvider.set(any())).willReturn(accessTokenProvider);

        underTest.deleteAccountEvent(request);

        then(accessTokenProvider).should().set(AccessToken.builder().userId(USER_ID).build());
        then(dao).should().deleteByUserId(USER_ID);
        then(accessTokenProvider).should().close();
    }
}