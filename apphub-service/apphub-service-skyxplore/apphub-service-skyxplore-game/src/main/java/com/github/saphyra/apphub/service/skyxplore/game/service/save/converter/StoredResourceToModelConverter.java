package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceToModelConverter {
    public List<StoredResourceModel> convert(Map<String, StoredResource> storedResources, UUID gameId) {
        return storedResources.values()
            .stream()
            .map(storedResource -> convert(storedResource, gameId))
            .collect(Collectors.toList());
    }

    public StoredResourceModel convert(StoredResource storedResource, UUID gameId) {
        StoredResourceModel model = new StoredResourceModel();
        model.setId(storedResource.getStoredResourceId());
        model.setGameId(gameId);
        model.setType(GameItemType.STORED_RESOURCE);
        model.setLocation(storedResource.getLocation());
        model.setLocationType(storedResource.getLocationType().name());
        model.setDataId(storedResource.getDataId());
        model.setAmount(storedResource.getAmount());
        return model;
    }
}
