package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class DeconstructionDao extends AbstractDao<DeconstructionEntity, DeconstructionModel, String, DeconstructionRepository> {
    private final UuidConverter uuidConverter;

    public DeconstructionDao(DeconstructionConverter converter, DeconstructionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<DeconstructionModel> findById(UUID id) {
        return findById(uuidConverter.convertDomain(id));
    }

    public List<DeconstructionModel> getByExternalReference(UUID externalReference) {
        return converter.convertEntity(repository.getByExternalReference(uuidConverter.convertDomain(externalReference)));
    }

    public void deleteById(UUID deconstructionId) {
        deleteById(uuidConverter.convertDomain(deconstructionId));
    }
}
