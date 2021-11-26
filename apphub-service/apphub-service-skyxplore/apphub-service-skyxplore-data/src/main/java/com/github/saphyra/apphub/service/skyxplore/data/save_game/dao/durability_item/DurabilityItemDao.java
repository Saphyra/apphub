package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DurabilityItemDao extends AbstractDao<DurabilityItemEntity, DurabilityItemModel, String, DurabilityItemRepository> {
    private final UuidConverter uuidConverter;

    public DurabilityItemDao(DurabilityItemConverter converter, DurabilityItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<DurabilityItemModel> findById(UUID durabilityItemId) {
        return findById(uuidConverter.convertDomain(durabilityItemId));
    }

    public List<DurabilityItemModel> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }

    public void deleteById(UUID durabilityItemId) {
        repository.deleteById(uuidConverter.convertDomain(durabilityItemId));
    }
}
