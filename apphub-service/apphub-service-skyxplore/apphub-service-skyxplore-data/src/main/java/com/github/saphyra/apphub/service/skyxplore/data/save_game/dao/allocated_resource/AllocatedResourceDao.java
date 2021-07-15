package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;


import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AllocatedResourceDao extends AbstractDao<AllocatedResourceEntity, AllocatedResourceModel, String, AllocatedResourceRepository> {
    private final UuidConverter uuidConverter;

    public AllocatedResourceDao(AllocatedResourceConverter converter, AllocatedResourceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<AllocatedResourceModel> findById(UUID allocatedResourceId) {
        return findById(uuidConverter.convertDomain(allocatedResourceId));
    }

    public List<AllocatedResourceModel> getByLocation(UUID location) {
        return converter.convertEntity(repository.getByLocation(uuidConverter.convertDomain(location)));
    }
}
