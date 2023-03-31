package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface DeconstructionRepository extends CrudRepository<DeconstructionEntity, String> {
    void deleteByGameId(String gameId);

    List<DeconstructionEntity> getByExternalReference(String externalReference);

    //TODO unit test
    List<DeconstructionEntity> getByGameId(String gameId, PageRequest pageRequest);
}
