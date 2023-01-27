package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameCleanupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreDataEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameCleanupService gameCleanupService;

    @Mock
    private DeleteByUserIdDao deleteByUserIdDao;

    private SkyXploreDataEventControllerImpl underTest;

    @BeforeEach
    public void setUp() {
        underTest = new SkyXploreDataEventControllerImpl(gameCleanupService, Arrays.asList(deleteByUserIdDao));
    }

    @Test
    public void deleteGames() {
        underTest.deleteGamesMarkedForDeletion();

        verify(gameCleanupService).deleteMarkedGames();
    }

    @Test
    public void deleteAccountEvent() {
        underTest.deleteAccountEvent(SendEventRequest.<DeleteAccountEvent>builder().payload(new DeleteAccountEvent(USER_ID)).build());

        verify(deleteByUserIdDao).deleteByUserId(USER_ID);
    }
}