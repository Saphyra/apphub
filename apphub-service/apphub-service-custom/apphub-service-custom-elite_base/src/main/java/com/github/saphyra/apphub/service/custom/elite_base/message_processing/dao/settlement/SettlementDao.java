package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SettlementDao extends AbstractDao<SettlementEntity, Settlement, String, SettlementRepository> {
    private final UuidConverter uuidConverter;

    SettlementDao(SettlementConverter converter, SettlementRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Settlement> findByStarSystemIdAndSettlementName(UUID starSystemId, String settlementName) {
        return converter.convertEntity(repository.findByStarSystemIdAndSettlementName(uuidConverter.convertDomain(starSystemId), settlementName));
    }
}
