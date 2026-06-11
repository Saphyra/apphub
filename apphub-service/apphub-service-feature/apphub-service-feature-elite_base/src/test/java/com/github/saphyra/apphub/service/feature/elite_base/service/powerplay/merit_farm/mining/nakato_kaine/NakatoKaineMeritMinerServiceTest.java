package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmRequest;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NakatoKaineMeritMinerServiceTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private StarSystemDataDao starSystemDataDao;

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ReinforcementOfferCollector reinforcementOfferCollector;

    @Mock
    private AcquisitionOfferCollector acquisitionOfferCollector;

    @InjectMocks
    private NakatoKaineMeritMinerService underTest;

    @BeforeEach
    void setUp() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
    }

    @Test
    void getLocations_filtersResponses() {
        MiningMeritFarmResponse passingResponse = createResponse(ReserveLevel.MAJOR, 100, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.REINFORCEMENT);
        MiningMeritFarmResponse activityMismatch = createResponse(ReserveLevel.MAJOR, 100, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.ACQUISITION);
        MiningMeritFarmResponse lowReserve = createResponse(ReserveLevel.LOW, 100, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.REINFORCEMENT);
        MiningMeritFarmResponse lowPrice = createResponse(ReserveLevel.MAJOR, 99, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.REINFORCEMENT);
        MiningMeritFarmResponse lowDemand = createResponse(ReserveLevel.MAJOR, 100, 49, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.REINFORCEMENT);
        MiningMeritFarmResponse stale = createResponse(ReserveLevel.MAJOR, 100, 50, CURRENT_TIME.minusHours(2), PowerplayActivityType.REINFORCEMENT);

        given(starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)).willReturn(List.of());
        given(reinforcementOfferCollector.getReinforcement(anyList())).willReturn(List.of(passingResponse, lowReserve, lowPrice, lowDemand, stale));
        given(acquisitionOfferCollector.getAcquisition(anyList())).willReturn(List.of(activityMismatch));

        List<MiningMeritFarmResponse> result = underTest.getLocations(MiningMeritFarmRequest.builder()
            .minimumReserveLevel(ReserveLevel.COMMON)
            .minimumPrice(100)
            .minimumDemand(50)
            .maxTimeSinceLastUpdated(Duration.ofHours(1))
            .powerplayActivity(PowerplayActivityType.REINFORCEMENT)
            .build());

        assertThat(result).containsExactly(passingResponse);
    }

    @Test
    void getLocations_includesAllActivitiesWhenRequestActivityIsNull() {
        MiningMeritFarmResponse reinforcement = createResponse(ReserveLevel.MAJOR, 100, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.REINFORCEMENT);
        MiningMeritFarmResponse acquisition = createResponse(ReserveLevel.MAJOR, 100, 50, CURRENT_TIME.minusMinutes(20), PowerplayActivityType.ACQUISITION);

        given(starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)).willReturn(List.of());
        given(reinforcementOfferCollector.getReinforcement(anyList())).willReturn(List.of(reinforcement));
        given(acquisitionOfferCollector.getAcquisition(anyList())).willReturn(List.of(acquisition));

        List<MiningMeritFarmResponse> result = underTest.getLocations(MiningMeritFarmRequest.builder()
            .minimumReserveLevel(ReserveLevel.COMMON)
            .minimumPrice(100)
            .minimumDemand(50)
            .maxTimeSinceLastUpdated(Duration.ofHours(1))
            .powerplayActivity(null)
            .build());

        assertThat(result).containsExactlyInAnyOrder(reinforcement, acquisition);
    }

    @Test
    void getLocations_routesSystemsToCollectorsByPowerplayState() {
        StarSystemData exploited = StarSystemData.builder().starSystemId(UUID.randomUUID()).powerplayState(PowerplayState.EXPLOITED).build();
        StarSystemData fortified = StarSystemData.builder().starSystemId(UUID.randomUUID()).powerplayState(PowerplayState.FORTIFIED).build();
        StarSystemData stronghold = StarSystemData.builder().starSystemId(UUID.randomUUID()).powerplayState(PowerplayState.STRONGHOLD).build();
        StarSystemData controlled = StarSystemData.builder().starSystemId(UUID.randomUUID()).powerplayState(PowerplayState.CONTROLLED).build();

        given(starSystemDataDao.getByControllingPower(Power.NAKATO_KAINE)).willReturn(List.of(exploited, fortified, stronghold, controlled));
        given(reinforcementOfferCollector.getReinforcement(anyList())).willReturn(List.of());
        given(acquisitionOfferCollector.getAcquisition(anyList())).willReturn(List.of());

        underTest.getLocations(MiningMeritFarmRequest.builder()
            .minimumReserveLevel(ReserveLevel.UNKNOWN)
            .minimumPrice(0)
            .minimumDemand(0)
            .maxTimeSinceLastUpdated(Duration.ofDays(1))
            .build());

        ArgumentCaptor<List<StarSystemData>> reinforcementCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<StarSystemData>> acquisitionCaptor = ArgumentCaptor.forClass(List.class);

        then(reinforcementOfferCollector).should().getReinforcement(reinforcementCaptor.capture());
        then(acquisitionOfferCollector).should().getAcquisition(acquisitionCaptor.capture());

        assertThat(reinforcementCaptor.getValue()).containsExactlyInAnyOrder(exploited, fortified, stronghold);
        assertThat(acquisitionCaptor.getValue()).containsExactlyInAnyOrder(fortified, stronghold);
    }

    private MiningMeritFarmResponse createResponse(
        ReserveLevel reserveLevel,
        int price,
        int demand,
        LocalDateTime lastUpdate,
        PowerplayActivityType activityType
    ) {
        return MiningMeritFarmResponse.builder()
            .reserveLevel(reserveLevel)
            .price(price)
            .demand(demand)
            .lastUpdate(lastUpdate)
            .activityType(activityType)
            .build();
    }
}