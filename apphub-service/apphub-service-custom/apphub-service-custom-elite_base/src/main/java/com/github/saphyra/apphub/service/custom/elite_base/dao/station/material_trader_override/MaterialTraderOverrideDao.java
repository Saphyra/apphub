package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MaterialTraderOverrideDao extends AbstractDao<MaterialTraderOverrideEntity, MaterialTraderOverride, String, MaterialTraderOverrideRepository> {
    private final UuidConverter uuidConverter;

    MaterialTraderOverrideDao(MaterialTraderOverrideConverter converter, MaterialTraderOverrideRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    //TODO unit test
    public Optional<MaterialTraderOverride> findById(UUID stationId) {
        return findById(uuidConverter.convertDomain(stationId));
    }

    //TODO unit test
    public MaterialTraderOverride findByIdValidated(UUID stationId) {
        return findById(stationId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "MaterialTraderOverride not found by stationId {}" + stationId));
    }
}
