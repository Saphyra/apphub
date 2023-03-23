package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceDetailsQueryService {
    private final ResourceDataService resourceDataService;
    private final ResourceDetailsResponseMapper resourceDetailsResponseMapper;

    List<ResourceDetailsResponse> getResourceDetails(GameData gameData, UUID location, StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(resourceData -> resourceDetailsResponseMapper.createResourceData(gameData, location, resourceData))
            .filter(ResourceDetailsResponse::valuePresent)
            .collect(Collectors.toList());
    }
}
