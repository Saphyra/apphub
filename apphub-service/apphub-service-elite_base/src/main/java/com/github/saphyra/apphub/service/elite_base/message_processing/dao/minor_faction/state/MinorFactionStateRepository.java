package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface MinorFactionStateRepository extends CrudRepository<MinorFactionStateEntity, MinorFactionStateEntityId> {
    List<MinorFactionStateEntity> getByMinorFactionIdAndStatus(String minorFactionId, StateStatus status);

    List<MinorFactionStateEntity> getByMinorFactionId(String minorFactionId);
}
