package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConstructionDao extends AbstractDao<ConstructionEntity, ConstructionModel, String, ConstructionRepository> {
    private final UuidConverter uuidConverter;

    public ConstructionDao(ConstructionConverter converter, ConstructionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<ConstructionModel> findById(UUID constructionId) {
        return findById(uuidConverter.convertDomain(constructionId));
    }

    public List<ConstructionModel> getByExternalReference(UUID externalReference) {
        return converter.convertEntity(repository.getByExternalReference(uuidConverter.convertDomain(externalReference)));
    }

    public void deleteById(UUID constructionId) {
        deleteById(uuidConverter.convertDomain(constructionId));
    }

    public List<ConstructionModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
