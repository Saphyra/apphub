package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting.query;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AvailableResourcesMapper {
    private final ResourceDataService resourceDataService;

    List<String> getAvailableResources(List<StorageSetting> storageSettings) {
        List<String> currentResources = storageSettings.stream()
            .map(StorageSetting::getDataId)
            .collect(Collectors.toList());

        return resourceDataService.keySet()
            .stream()
            .filter(dataId -> !currentResources.contains(dataId))
            .collect(Collectors.toList());
    }
}
