package com.github.saphyra.apphub.service.custom.elite_base.service.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverride;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override.MaterialTraderOverrideDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreateMaterialTraderOverrideRequestValidatorTest {
    private static final UUID STATION_ID = UUID.randomUUID();

    @Mock
    private MaterialTraderOverrideDao materialTraderOverrideDao;

    @InjectMocks
    private CreateMaterialTraderOverrideRequestValidator underTest;

    @Mock
    private MaterialTraderOverride materialTraderOverride;

    @Test
    void nullStationId() {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(null)
            .materialType(MaterialType.RAW)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "stationId", "must not be null");
    }

    @Test
    void nullMaterialType() {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(STATION_ID)
            .materialType(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "materialType", "must not be null");
    }

    @ParameterizedTest
    @EnumSource(value = MaterialType.class, mode = EnumSource.Mode.EXCLUDE, names = {"RAW", "ENCODED", "MANUFACTURED", "NONE"})
    void unacceptableMaterialType(MaterialType materialType) {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(STATION_ID)
            .materialType(materialType)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "materialType", "unacceptable value");
    }

    @Test
    void overrideAlreadyExists() {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.RAW)
            .build();
        given(materialTraderOverrideDao.findById(STATION_ID)).willReturn(Optional.of(materialTraderOverride));

        ExceptionValidator.validateNotLoggedException(() -> underTest.validate(request), HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    void valid() {
        CreateMaterialTraderOverrideRequest request = CreateMaterialTraderOverrideRequest.builder()
            .stationId(STATION_ID)
            .materialType(MaterialType.RAW)
            .build();
        given(materialTraderOverrideDao.findById(STATION_ID)).willReturn(Optional.empty());

        underTest.validate(request);
    }
}