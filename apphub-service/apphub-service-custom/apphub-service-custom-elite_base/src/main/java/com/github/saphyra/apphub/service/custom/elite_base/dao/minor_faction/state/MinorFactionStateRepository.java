package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface MinorFactionStateRepository extends CrudRepository<MinorFactionStateEntity, MinorFactionStateEntityId> {
    List<MinorFactionStateEntity> getByMinorFactionIdAndStatus(String minorFactionId, StateStatus status);

    List<MinorFactionStateEntity> getByMinorFactionId(String minorFactionId);
}
