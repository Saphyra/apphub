package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StoredResourceToModelConverter {
    public List<StoredResourceModel> convert(Map<String, StoredResource> storedResources, Game game) {
        return storedResources.values()
            .stream()
            .map(storedResource -> convert(storedResource, game))
            .collect(Collectors.toList());
    }

    public StoredResourceModel convert(StoredResource storedResource, Game game) {
        StoredResourceModel model = new StoredResourceModel();
        model.setId(storedResource.getStoredResourceId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.STORED_RESOURCE);
        model.setLocation(storedResource.getLocation());
        model.setLocationType(storedResource.getLocationType().name());
        model.setDataId(storedResource.getDataId());
        model.setAmount(storedResource.getAmount());
        return model;
    }
}
