package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ResourceDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceDetailsQueryService {
    private final ResourceDataService resourceDataService;
    private final ResourceDetailsResponseMapper resourceDetailsResponseMapper;

    List<ResourceDetailsResponse> getResourceDetails(Planet planet, StorageType storageType) {
        return resourceDataService.getByStorageType(storageType)
            .stream()
            .map(resourceData -> resourceDetailsResponseMapper.createResourceData(resourceData, planet.getStorageDetails()))
            .filter(ResourceDetailsResponse::valuePresent)
            .collect(Collectors.toList());
    }
}
