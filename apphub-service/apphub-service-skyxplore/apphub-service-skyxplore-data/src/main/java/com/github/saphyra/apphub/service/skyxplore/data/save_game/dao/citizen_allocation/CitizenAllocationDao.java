package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;


import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CitizenAllocationDao extends AbstractDao<CitizenAllocationEntity, CitizenAllocationModel, String, CitizenAllocationRepository> {
    private final UuidConverter uuidConverter;

    public CitizenAllocationDao(CitizenAllocationConverter converter, CitizenAllocationRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public void deleteById(UUID citizenAllocationId) {
        deleteById(uuidConverter.convertDomain(citizenAllocationId));
    }

    public List<CitizenAllocationModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
