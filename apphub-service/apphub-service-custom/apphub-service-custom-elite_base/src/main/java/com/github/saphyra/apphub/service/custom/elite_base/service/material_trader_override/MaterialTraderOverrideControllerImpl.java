package com.github.saphyra.apphub.service.custom.elite_base.service.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.material_trader.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.api.elite_base.server.EliteBaseAccountController;
import com.github.saphyra.apphub.api.elite_base.server.MaterialTraderOverrideController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverride;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class MaterialTraderOverrideControllerImpl implements MaterialTraderOverrideController {
    private final EliteBaseAccountController eliteBaseAccountController;
    private final MaterialTraderOverrideDao materialTraderOverrideDao;
    private final CreateMaterialTraderOverrideRequestValidator createMaterialTraderOverrideRequestValidator;
    private final MaterialTraderOverrideFactory materialTraderOverrideFactory;

    @Override
    public void createOverride(CreateMaterialTraderOverrideRequest request, AccessTokenHeader accessTokenHeader) {
        boolean isAdmin = eliteBaseAccountController.isAdmin(accessTokenHeader);
        log.info("{} wants to create materialTraderOverride based on request {}. Admin: {}", accessTokenHeader.getUserId(), request, isAdmin);

        createMaterialTraderOverrideRequestValidator.validate(request);

        MaterialTraderOverride materialTraderOverride = materialTraderOverrideFactory.create(request, isAdmin);

        materialTraderOverrideDao.save(materialTraderOverride);
    }

    @Override
    public void deleteOverride(UUID stationId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete materialTraderOverride {}", accessTokenHeader.getUserId(), stationId);

        MaterialTraderOverride materialTraderOverride = materialTraderOverrideDao.findByIdValidated(stationId);

        if (materialTraderOverride.isVerified()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.LOCKED, ErrorCode.INVALID_STATUS, "MaterialTraderOverride for stationId " + stationId + " is already verified.");
        }

        materialTraderOverrideDao.delete(materialTraderOverride);
    }

    @Override
    public void verifyOverride(UUID stationId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to verify materialTraderOverride {}", accessTokenHeader.getUserId(), stationId);

        MaterialTraderOverride materialTraderOverride = materialTraderOverrideDao.findByIdValidated(stationId);

        if (materialTraderOverride.isVerified()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.LOCKED, ErrorCode.INVALID_STATUS, "MaterialTraderOverride for stationId " + stationId + " is already verified.");
        }

        materialTraderOverride.setVerified(true);

        materialTraderOverrideDao.save(materialTraderOverride);
    }
}
