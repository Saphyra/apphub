package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConvoyDao extends AbstractDao<ConvoyEntity, ConvoyModel, String, ConvoyRepository> {
    private final UuidConverter uuidConverter;

    ConvoyDao(ConvoyConverter converter, ConvoyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID reservedStorageId) {
        deleteById(uuidConverter.convertDomain(reservedStorageId));
    }

    public List<ConvoyModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
