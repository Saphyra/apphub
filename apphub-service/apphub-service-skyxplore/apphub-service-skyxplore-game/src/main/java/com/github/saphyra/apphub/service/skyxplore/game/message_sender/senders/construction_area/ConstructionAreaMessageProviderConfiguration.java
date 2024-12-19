package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
class ConstructionAreaMessageProviderConfiguration {
    private final MessageSenderUtil messageSenderUtil;
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final BuildingModuleQueryService buildingModuleQueryService;

    @Bean
    DefaultConstructionAreaMessageProvider<List<BuildingModuleResponse>> buildingModulesConstructionAreaMessageProvider(){
        return DefaultConstructionAreaMessageProvider.<List<BuildingModuleResponse>>builder()
            .messageSenderUtil(messageSenderUtil)
            .itemKey(GameConstants.ITEM_KEY_BUILDING_MODULES)
            .responseProvider(buildingModuleQueryService::getBuildingModulesOfConstructionArea)
            .dateTimeUtil(dateTimeUtil)
            .pollingInterval(gameProperties.getMessageDelay().getConstructionAreaBuildingModules())
            .build();
    }
}
