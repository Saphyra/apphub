package com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSatietyProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SatietyDecreaseServiceTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer SATIETY_DECREASED_PER_SECONDS = 524;
    private static final Integer CURRENT_SATIETY = 3566;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private CitizenToResponseConverter citizenToResponseConverter;

    @InjectMocks
    private SatietyDecreaseService underTest;

    @Mock
    private Game game;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Universe universe;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSatietyProperties satietyProperties;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private CitizenResponse citizenResponse;

    @Test
    public void processGame() {
        given(game.getUniverse()).willReturn(universe);
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, solarSystem));
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet));
        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getPopulation()).willReturn(CollectionUtils.singleValueMap(CITIZEN_ID, citizen));
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSatiety()).willReturn(satietyProperties);
        given(satietyProperties.getSatietyDecreasedPerSecond()).willReturn(SATIETY_DECREASED_PER_SECONDS);
        given(citizen.getSatiety()).willReturn(CURRENT_SATIETY);
        given(citizenToModelConverter.convert(citizen, GAME_ID)).willReturn(citizenModel);
        given(planet.getOwner()).willReturn(USER_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(citizenToResponseConverter.convert(citizen)).willReturn(citizenResponse);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        underTest.processGame(game, syncCache);

        verify(citizen).setSatiety(CURRENT_SATIETY - SATIETY_DECREASED_PER_SECONDS);
        verify(syncCache).saveGameItem(citizenModel);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetCitizenModified(USER_ID, PLANET_ID, citizenResponse);
    }
}