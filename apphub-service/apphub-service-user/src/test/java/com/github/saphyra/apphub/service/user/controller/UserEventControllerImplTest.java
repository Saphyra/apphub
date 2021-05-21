package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.event.PageVisitedEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final String PAGE_URL = "page-url";

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserEventControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Test
    public void deleteAccountEvent() {
        DeleteAccountEvent event = new DeleteAccountEvent(USER_ID);
        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(event)
            .build();

        underTest.deleteAccountEvent(sendEventRequest);

        verify(accessTokenDao).deleteByUserId(USER_ID);
        verify(userDao).deleteById(USER_ID);
        verify(roleDao).deleteByUserId(USER_ID);
    }

    @Test
    public void pageVisitedEvent_accessTokenNotFound() {
        underTest.pageVisitedEvent(SendEventRequest.<PageVisitedEvent>builder().payload(new PageVisitedEvent(UUID.randomUUID(), null)).build());

        verify(accessTokenDao, times(0)).save(any());
    }

    @Test
    public void pageVisitedEvent() {
        given(accessTokenDao.findById(ACCESS_TOKEN_ID)).willReturn(Optional.of(accessToken));

        underTest.pageVisitedEvent(SendEventRequest.<PageVisitedEvent>builder().payload(new PageVisitedEvent(ACCESS_TOKEN_ID, PAGE_URL)).build());

        verify(accessToken).setLastVisitedPage(PAGE_URL);
        verify(accessTokenDao).save(accessToken);
    }
}