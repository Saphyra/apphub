package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DurabilityDao extends AbstractDao<DurabilityEntity, DurabilityModel, String, DurabilityRepository> {
    private final UuidConverter uuidConverter;

    public DurabilityDao(DurabilityItemConverter converter, DurabilityRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<DurabilityModel> findById(UUID durabilityItemId) {
        return findById(uuidConverter.convertDomain(durabilityItemId));
    }

    public void deleteById(UUID durabilityItemId) {
        repository.deleteById(uuidConverter.convertDomain(durabilityItemId));
    }
}
