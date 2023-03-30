package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;


import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

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
}
