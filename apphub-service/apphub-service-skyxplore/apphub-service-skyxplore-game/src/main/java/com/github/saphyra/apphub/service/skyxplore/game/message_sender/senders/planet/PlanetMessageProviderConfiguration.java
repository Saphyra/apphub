package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueFacade;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.SurfaceResponseQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PlanetMessageProviderConfiguration {
    private final MessageSenderUtil messageSenderUtil;
    private final SurfaceResponseQueryService surfaceResponseQueryService;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final QueueFacade queueFacade;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;

    @Bean
    DefaultPlanetMessageProvider<List<SurfaceResponse>> surfacePlanetMessageProvider() {
        return DefaultPlanetMessageProvider.<List<SurfaceResponse>>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_SURFACES)
            .responseProvider(surfaceResponseQueryService::getSurfaceOfPlanet)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getPlanetSurface())
            .build();
    }

    @Bean
    DefaultPlanetMessageProvider<PlanetStorageResponse> storagePlanetMessageProvider() {
        return DefaultPlanetMessageProvider.<PlanetStorageResponse>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_STORAGE)
            .responseProvider(planetStorageOverviewQueryService::getStorage)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getPlanetStorage())
            .build();
    }

    @Bean
    DefaultPlanetMessageProvider<List<QueueResponse>> queuePlanetMessageProvider() {
        return DefaultPlanetMessageProvider.<List<QueueResponse>>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_QUEUE)
            .responseProvider(queueFacade::getQueueOfPlanet)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getPlanetQueue())
            .build();
    }

    @Bean
    DefaultPlanetMessageProvider<Map<String, PlanetBuildingOverviewResponse>> buildingPlanetMessageProvider() {
        return DefaultPlanetMessageProvider.<Map<String, PlanetBuildingOverviewResponse>>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_BUILDINGS)
            .responseProvider(planetBuildingOverviewQueryService::getBuildingOverview)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getPlanetBuilding())
            .build();
    }

    @Bean
    DefaultPlanetMessageProvider<PlanetPopulationOverviewResponse> populationPlanetMessageProvider() {
        return DefaultPlanetMessageProvider.<PlanetPopulationOverviewResponse>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_POPULATION)
            .responseProvider(planetPopulationOverviewQueryService::getPopulationOverview)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getPlanetStorage())
            .build();
    }
}
