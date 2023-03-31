package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DeconstructionDao extends AbstractDao<DeconstructionEntity, DeconstructionModel, String, DeconstructionRepository> {
    private final UuidConverter uuidConverter;

    public DeconstructionDao(DeconstructionConverter converter, DeconstructionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<DeconstructionModel> findById(UUID deconstructionId) {
        return findById(uuidConverter.convertDomain(deconstructionId));
    }

    public List<DeconstructionModel> getByExternalReference(UUID externalReference) {
        return converter.convertEntity(repository.getByExternalReference(uuidConverter.convertDomain(externalReference)));
    }

    public void deleteById(UUID deconstructionId) {
        deleteById(uuidConverter.convertDomain(deconstructionId));
    }

    public List<DeconstructionModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
