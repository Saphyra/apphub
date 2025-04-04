package com.github.saphyra.apphub.service.custom.elite_base.service.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateMaterialTraderOverrideRequestValidator {
    private static final EnumSet<MaterialType> ACCEPTED_MATERIAL_TYPES = EnumSet.of(MaterialType.RAW, MaterialType.ENCODED, MaterialType.MANUFACTURED);

    private final MaterialTraderOverrideDao materialTraderOverrideDao;

    void validate(CreateMaterialTraderOverrideRequest request) {
        ValidationUtil.notNull(request.getStationId(), "stationId");
        ValidationUtil.notNull(request.getMaterialType(), "materialType");

        if (!ACCEPTED_MATERIAL_TYPES.contains(request.getMaterialType())) {
            throw ExceptionFactory.invalidParam("materialType", "unacceptable value");
        }

        if (materialTraderOverrideDao.findById(request.getStationId()).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "MaterialTraderOverride already exists for stationId " + request.getStationId());
        }
    }
}
