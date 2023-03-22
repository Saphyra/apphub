package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
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
public class AllocatedResourceToModelConverter {
    public List<AllocatedResourceModel> convert(UUID gameId, Collection<AllocatedResource> allocatedResources) {
        return allocatedResources.stream()
            .map(allocatedResource -> convert(gameId, allocatedResource))
            .collect(Collectors.toList());
    }

    public AllocatedResourceModel convert(UUID gameId, AllocatedResource allocatedResource) {
        AllocatedResourceModel model = new AllocatedResourceModel();
        model.setId(allocatedResource.getAllocatedResourceId());
        model.setGameId(gameId);
        model.setType(GameItemType.ALLOCATED_RESOURCE);
        model.setLocation(allocatedResource.getLocation());
        model.setExternalReference(allocatedResource.getExternalReference());
        model.setDataId(allocatedResource.getDataId());
        model.setAmount(allocatedResource.getAmount());
        return model;
    }
}
