package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceConverter {
    public List<StoredResourceModel> toModel(UUID gameId, Collection<StoredResource> storedResources) {
        return storedResources.stream()
            .map(storedResource -> toModel(gameId, storedResource))
            .collect(Collectors.toList());
    }

    public StoredResourceModel toModel(UUID gameId, StoredResource storedResource) {
        StoredResourceModel model = new StoredResourceModel();
        model.setId(storedResource.getStoredResourceId());
        model.setGameId(gameId);
        model.setType(GameItemType.STORED_RESOURCE);
        model.setLocation(storedResource.getLocation());
        model.setDataId(storedResource.getDataId());
        model.setAmount(storedResource.getAmount());
        return model;
    }
}
