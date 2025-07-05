package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConstructionAreaDao extends AbstractDao<ConstructionAreaEntity, ConstructionAreaModel, String, ConstructionAreaRepository> {
    private final UuidConverter uuidConverter;

    ConstructionAreaDao(ConstructionAreaConverter converter, ConstructionAreaRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID constructionAreaId) {
        deleteById(uuidConverter.convertDomain(constructionAreaId));
    }

    public List<ConstructionAreaModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
