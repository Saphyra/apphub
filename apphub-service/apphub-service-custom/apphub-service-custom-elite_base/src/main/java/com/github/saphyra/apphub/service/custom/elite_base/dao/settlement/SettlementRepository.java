package com.github.saphyra.apphub.service.custom.elite_base.dao.settlement;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@Deprecated(forRemoval = true)
interface SettlementRepository extends CrudRepository<SettlementEntity, String> {
    Optional<SettlementEntity> findByStarSystemIdAndSettlementName(String starSystemId, String settlementName);
}
