package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AcquisitionOfferCollectorTest {
    private static final UUID SOURCE_SYSTEM_ID = UUID.randomUUID();
    private static final UUID TARGET_SYSTEM_ID = UUID.randomUUID();
    private static final UUID STATION_ID = UUID.randomUUID();
    private static final String SOURCE_STAR_NAME = "source-star-name";
    private static final String TARGET_STAR_NAME = "target-star-name";
    private static final String STATION_NAME = "station-name";
    private static final String COMMODITY_NAME = "commodity-name";
    private static final Integer DEMAND = 342;
    private static final Integer PRICE = 24;
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private MetallicRingService metallicRingService;

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private StationDao stationDao;

    @Mock
    private OfferService offerService;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @Mock
    private AcquisitionTargetProvider acquisitionTargetProvider;

    @InjectMocks
    private AcquisitionOfferCollector underTest;

    @Mock
    private StarSystemData sourceSystemData;

    @Mock
    private StarSystem sourceSystem;

    @Mock
    private StarSystem targetSystem;

    @Mock
    private Station station;

    @Mock
    private Commodity commodity;

    @Mock
    private LastUpdate lastUpdate;

    @Test
    void getAcquisition() {
        given(sourceSystemData.getStarSystemId()).willReturn(SOURCE_SYSTEM_ID);
        given(metallicRingService.getSystemsWithMetallicRing(List.of(SOURCE_SYSTEM_ID))).willReturn(Map.of(SOURCE_SYSTEM_ID, ReserveLevel.COMMON));
        given(starSystemDao.getByIds(Set.of(SOURCE_SYSTEM_ID))).willReturn(List.of(sourceSystem));
        given(sourceSystem.getId()).willReturn(SOURCE_SYSTEM_ID);
        given(acquisitionTargetProvider.getAcquisitionTargets(anyCollection(), eq(List.of(sourceSystemData)))).willReturn(Map.of(SOURCE_SYSTEM_ID, List.of(targetSystem)));
        given(targetSystem.getId()).willReturn(TARGET_SYSTEM_ID);
        given(stationDao.getByStarSystemIds(List.of(TARGET_SYSTEM_ID))).willReturn(List.of(station));
        given(offerService.getOffers(List.of(station), PowerplayActivityType.ACQUISITION)).willReturn(List.of(commodity));
        given(station.getId()).willReturn(STATION_ID);
        given(sourceSystem.getStarName()).willReturn(SOURCE_STAR_NAME);
        given(targetSystem.getStarName()).willReturn(TARGET_STAR_NAME);
        given(commodity.getExternalReference()).willReturn(STATION_ID);
        given(station.getStationName()).willReturn(STATION_NAME);
        given(commodity.getDemand()).willReturn(DEMAND);
        given(commodity.getItemName()).willReturn(COMMODITY_NAME);
        given(commodity.getBuyPrice()).willReturn(PRICE);
        given(lastUpdateDao.findByIdOrDefault(STATION_ID, ObjectType.COMMODITY)).willReturn(lastUpdate);
        given(lastUpdate.getLastUpdate()).willReturn(LAST_UPDATE);
        given(station.getStarSystemId()).willReturn(TARGET_SYSTEM_ID);

        CustomAssertions.singleListAssertThat(underTest.getAcquisition(List.of(sourceSystemData)))
            .returns(SOURCE_SYSTEM_ID, MiningMeritFarmResponse::getSourceStarSystemId)
            .returns(SOURCE_STAR_NAME, MiningMeritFarmResponse::getSourceStarSystemName)
            .returns(TARGET_SYSTEM_ID, MiningMeritFarmResponse::getTargetStarSystemId)
            .returns(TARGET_STAR_NAME, MiningMeritFarmResponse::getTargetStarSystemName)
            .returns(ReserveLevel.COMMON, MiningMeritFarmResponse::getReserveLevel)
            .returns(STATION_ID, MiningMeritFarmResponse::getStationId)
            .returns(STATION_NAME, MiningMeritFarmResponse::getStationName)
            .returns(COMMODITY_NAME, MiningMeritFarmResponse::getCommodityName)
            .returns(DEMAND, MiningMeritFarmResponse::getDemand)
            .returns(PRICE, MiningMeritFarmResponse::getPrice)
            .returns(LAST_UPDATE, MiningMeritFarmResponse::getLastUpdate)
            .returns(PowerplayActivityType.ACQUISITION, MiningMeritFarmResponse::getActivityType);
    }
}