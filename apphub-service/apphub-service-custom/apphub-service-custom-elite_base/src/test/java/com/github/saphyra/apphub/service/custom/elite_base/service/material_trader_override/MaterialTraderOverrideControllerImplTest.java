package com.github.saphyra.apphub.service.custom.elite_base.service.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.api.elite_base.server.EliteBaseAccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverride;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MaterialTraderOverrideControllerImplTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Mock
    private EliteBaseAccountController eliteBaseAccountController;

    @Mock
    private MaterialTraderOverrideDao materialTraderOverrideDao;

    @Mock
    private CreateMaterialTraderOverrideRequestValidator createMaterialTraderOverrideRequestValidator;

    @Mock
    private MaterialTraderOverrideFactory materialTraderOverrideFactory;

    @InjectMocks
    private MaterialTraderOverrideControllerImpl underTest;

    @Mock
    private CreateMaterialTraderOverrideRequest request;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private MaterialTraderOverride materialTraderOverride;

    @Test
    void createOverride() {
        given(eliteBaseAccountController.isAdmin(accessTokenHeader)).willReturn(true);
        given(materialTraderOverrideFactory.create(request, true)).willReturn(materialTraderOverride);

        underTest.createOverride(request, accessTokenHeader);

        then(createMaterialTraderOverrideRequestValidator).should().validate(request);
        then(materialTraderOverrideDao).should().save(materialTraderOverride);
    }

    @Test
    void deleteOverride_verified() {
        given(materialTraderOverrideDao.findByIdValidated(STATION_ID)).willReturn(materialTraderOverride);
        given(materialTraderOverride.isVerified()).willReturn(true);

        ExceptionValidator.validateNotLoggedException(() -> underTest.deleteOverride(STATION_ID, accessTokenHeader), HttpStatus.LOCKED, ErrorCode.INVALID_STATUS);
    }

    @Test
    void deleteOverride() {
        given(materialTraderOverrideDao.findByIdValidated(STATION_ID)).willReturn(materialTraderOverride);
        given(materialTraderOverride.isVerified()).willReturn(false);

        underTest.deleteOverride(STATION_ID, accessTokenHeader);

        then(materialTraderOverrideDao).should().delete(materialTraderOverride);
    }

    @Test
    void verifyOverride_alreadyVerified() {
        given(materialTraderOverrideDao.findByIdValidated(STATION_ID)).willReturn(materialTraderOverride);
        given(materialTraderOverride.isVerified()).willReturn(true);

        ExceptionValidator.validateNotLoggedException(() -> underTest.verifyOverride(STATION_ID, accessTokenHeader), HttpStatus.LOCKED, ErrorCode.INVALID_STATUS);
    }

    @Test
    void verifyOverride() {
        given(materialTraderOverrideDao.findByIdValidated(STATION_ID)).willReturn(materialTraderOverride);
        given(materialTraderOverride.isVerified()).willReturn(false);

        underTest.verifyOverride(STATION_ID, accessTokenHeader);

        then(materialTraderOverride).should().setVerified(true);
        then(materialTraderOverrideDao).should().save(materialTraderOverride);
    }
}