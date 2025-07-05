package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StoredResourceDao extends AbstractDao<StoredResourceEntity, StoredResourceModel, String, StoredResourceRepository> {
    private final UuidConverter uuidConverter;

    public StoredResourceDao(StoredResourceConverter converter, StoredResourceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<StoredResourceModel> findById(UUID storedResourceId) {
        return findById(uuidConverter.convertDomain(storedResourceId));
    }

    public List<StoredResourceModel> getByLocation(UUID location) {
        return converter.convertEntity(repository.getByLocation(uuidConverter.convertDomain(location)));
    }

    public void deleteById(UUID storedResourceId) {
        deleteById(uuidConverter.convertDomain(storedResourceId));
    }

    public List<StoredResourceModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
