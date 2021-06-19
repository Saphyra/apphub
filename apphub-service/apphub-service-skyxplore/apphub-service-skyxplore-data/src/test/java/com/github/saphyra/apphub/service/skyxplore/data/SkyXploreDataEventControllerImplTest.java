package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreDataEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DeleteByUserIdDao deleteByUserIdDao;

    private SkyXploreDataEventControllerImpl underTest;

    @Before
    public void setUp() {
        underTest = new SkyXploreDataEventControllerImpl(Arrays.asList(deleteByUserIdDao));
    }

    @Test
    public void deleteAccountEvent() {
        underTest.deleteAccountEvent(SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build());

        verify(deleteByUserIdDao).deleteByUserId(USER_ID);
    }
}