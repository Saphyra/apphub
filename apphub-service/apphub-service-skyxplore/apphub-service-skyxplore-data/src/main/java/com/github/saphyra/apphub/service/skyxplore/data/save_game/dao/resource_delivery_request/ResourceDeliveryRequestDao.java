package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.resource_delivery_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ResourceDeliveryRequestDao extends AbstractDao<ResourceDeliveryRequestEntity, ResourceDeliveryRequestModel, String, ResourceDeliveryRequestRepository> {
    private final UuidConverter uuidConverter;

    ResourceDeliveryRequestDao(ResourceDeliveryRequestConverter converter, ResourceDeliveryRequestRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID id) {
        deleteById(uuidConverter.convertDomain(id));
    }

    public List<ResourceDeliveryRequestModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
