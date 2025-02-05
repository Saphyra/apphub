package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface SettlementRepository extends CrudRepository<SettlementEntity, String> {
    Optional<SettlementEntity> findByStarSystemIdAndSettlementName(String starSystemId, String settlementName);
}
