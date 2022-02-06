package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.PlanetTickProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ProcessTickServiceTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private TickCache tickCache;

    @Mock
    private PlanetTickProcessor planetTickProcessor;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private ProcessTickService.TickCacheItemFactory tickCacheItemFactory;

    @InjectMocks
    private ProcessTickService underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private Universe universe;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Test
    public void gamePaused() {
        given(game.isGamePaused()).willReturn(true);

        underTest.processTick(game);

        verifyNoInteractions(tickCache);
    }

    @Test
    public void hasDisconnectedPlayer() {
        given(game.isGamePaused()).willReturn(false);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
        given(player.isConnected()).willReturn(false);

        underTest.processTick(game);

        verifyNoInteractions(tickCache);
    }

    @Test
    public void processTick() {
        given(game.isGamePaused()).willReturn(false);
        given(game.getGameId()).willReturn(GAME_ID);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
        given(game.getUniverse()).willReturn(universe);
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, solarSystem));
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet));

        given(player.isConnected()).willReturn(true);

        given(tickCacheItemFactory.create()).willReturn(tickCacheItem);

        underTest.processTick(game);

        verify(tickCache).put(GAME_ID, tickCacheItem);
        verify(planetTickProcessor).processForPlanet(GAME_ID, planet);
        verify(tickCache).process(GAME_ID);
    }

    @Test
    public void error() {
        given(game.isGamePaused()).willReturn(false);
        given(game.getGameId()).willReturn(GAME_ID);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(PLAYER_ID, player));
        given(game.getUniverse()).willReturn(universe);
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, solarSystem));
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet));

        doThrow(new RuntimeException()).when(planetTickProcessor).processForPlanet(GAME_ID, planet);

        given(player.isConnected()).willReturn(true);

        given(tickCacheItemFactory.create()).willReturn(tickCacheItem);

        underTest.processTick(game);

        verify(tickCache).put(GAME_ID, tickCacheItem);
        verify(planetTickProcessor).processForPlanet(GAME_ID, planet);
        verify(tickCache).process(GAME_ID);

        verify(errorReporterService).report(anyString(), any());
    }
}