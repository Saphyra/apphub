package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlanetQueueControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 24;
    private static final String TYPE = "type";
    private static final UUID ITEM_ID = UUID.randomUUID();

    @Mock
    private QueueFacade queueFacade;

    @InjectMocks
    private PlanetQueueController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void setItemPriority() {
        underTest.setItemPriority(new OneParamRequest<>(PRIORITY), PLANET_ID, TYPE, ITEM_ID, accessTokenHeader);

        verify(queueFacade).setPriority(USER_ID, PLANET_ID, TYPE, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancelItem() {
        underTest.cancelItem(PLANET_ID, TYPE, ITEM_ID, accessTokenHeader);

        verify(queueFacade).cancelItem(USER_ID, PLANET_ID, TYPE, ITEM_ID);
    }
}