package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine.NakatoKaineMeritMinerService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PowerplayControllerImplTest {
    private static final String POWER = "NAKATO_KAINE";

    @Mock
    private NakatoKaineMeritMinerService nakatoKaineMeritMinerService;

    @InjectMocks
    private PowerplayControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private MiningMeritFarmResponse response;

    @Test
    void meritFarmGetMiningLocations() {
        MiningMeritFarmRequest request = validRequest();
        List<MiningMeritFarmResponse> result = List.of(response);
        given(nakatoKaineMeritMinerService.getLocations(request)).willReturn(result);

        assertThat(underTest.meritFarmGetMiningLocations(request, POWER, accessToken)).isEqualTo(result);
    }

    @Test
    void meritFarmGetMiningLocations_invalidPower() {
        MiningMeritFarmRequest request = validRequest();

        ExceptionValidator.validateInvalidParam(() -> underTest.meritFarmGetMiningLocations(request, "invalid", accessToken), "power", "failed to parse");
    }

    @Test
    void meritFarmGetMiningLocations_minimumReserveLevelNull() {
        MiningMeritFarmRequest request = validRequest();
        request.setMinimumReserveLevel(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.meritFarmGetMiningLocations(request, POWER, accessToken), "minimumReserveLevel", "must not be null");
    }

    @Test
    void meritFarmGetMiningLocations_minimumPriceTooLow() {
        MiningMeritFarmRequest request = validRequest();
        request.setMinimumPrice(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.meritFarmGetMiningLocations(request, POWER, accessToken), "minimumPrice", "too low");
    }

    @Test
    void meritFarmGetMiningLocations_minimumDemandTooLow() {
        MiningMeritFarmRequest request = validRequest();
        request.setMinimumDemand(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.meritFarmGetMiningLocations(request, POWER, accessToken), "minimumDemand", "too low");
    }

    @Test
    void meritFarmGetMiningLocations_maxTimeSinceLastUpdatedNull() {
        MiningMeritFarmRequest request = validRequest();
        request.setMaxTimeSinceLastUpdated(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.meritFarmGetMiningLocations(request, POWER, accessToken), "maxTimeSinceLastUpdated", "must not be null");
    }

    @Test
    void meritFarmGetMiningLocations_notImplementedPower() {
        MiningMeritFarmRequest request = validRequest();

        ExceptionValidator.validateNotLoggedException(() -> underTest.meritFarmGetMiningLocations(request, "ARCHON_DELAINE", accessToken), HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    private MiningMeritFarmRequest validRequest() {
        return MiningMeritFarmRequest.builder()
            .minimumReserveLevel(ReserveLevel.PRISTINE)
            .minimumPrice(0)
            .minimumDemand(0)
            .maxTimeSinceLastUpdated(Duration.ofHours(1))
            .powerplayActivity(PowerplayActivityType.ACQUISITION)
            .build();
    }
}