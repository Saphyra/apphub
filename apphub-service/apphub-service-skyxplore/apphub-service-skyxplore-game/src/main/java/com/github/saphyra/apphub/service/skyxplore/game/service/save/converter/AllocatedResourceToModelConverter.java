package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AllocatedResourceToModelConverter {
    public List<AllocatedResourceModel> convert(List<AllocatedResource> allocatedResources, Game game) {
        return allocatedResources.stream()
            .map(allocatedResource -> convert(allocatedResource, game))
            .collect(Collectors.toList());
    }

    public AllocatedResourceModel convert(AllocatedResource allocatedResource, Game game) {
        AllocatedResourceModel model = new AllocatedResourceModel();
        model.setId(allocatedResource.getAllocatedResourceId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.ALLOCATED_RESOURCE);
        model.setLocation(allocatedResource.getLocation());
        model.setLocationType(allocatedResource.getLocationType().name());
        model.setExternalReference(allocatedResource.getExternalReference());
        model.setDataId(allocatedResource.getDataId());
        model.setStorageType(allocatedResource.getStorageType().name());
        model.setAmount(allocatedResource.getAmount());
        return model;
    }
}
