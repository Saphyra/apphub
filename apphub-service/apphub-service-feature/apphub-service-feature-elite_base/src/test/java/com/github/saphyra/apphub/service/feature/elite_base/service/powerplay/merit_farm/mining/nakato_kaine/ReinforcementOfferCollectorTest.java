package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.StationDao;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReinforcementOfferCollectorTest {
    private static final UUID SYSTEM_ID = UUID.randomUUID();
    private static final UUID STATION_ID_1 = UUID.randomUUID();
    private static final UUID STATION_ID_2 = UUID.randomUUID();
    private static final String SYSTEM_NAME = "system-name";
    private static final String STATION_NAME_1 = "station-name-1";
    private static final String STATION_NAME_2 = "station-name-2";
    private static final String COMMODITY_NAME_1 = "commodity-name-1";
    private static final String COMMODITY_NAME_2 = "commodity-name-2";
    private static final Integer DEMAND_1 = 123;
    private static final Integer DEMAND_2 = 456;
    private static final Integer PRICE_1 = 789;
    private static final Integer PRICE_2 = 987;
    private static final LocalDateTime LAST_UPDATE_1 = LocalDateTime.now().minusMinutes(5);
    private static final LocalDateTime LAST_UPDATE_2 = LocalDateTime.now().minusMinutes(10);

    @Mock
    private MetallicRingService metallicRingService;

    @Mock
    private StationDao stationDao;

    @Mock
    private OfferService offerService;

    @Mock
    private StarSystemDao starSystemDao;

    @Mock
    private LastUpdateDao lastUpdateDao;

    @InjectMocks
    private ReinforcementOfferCollector underTest;

    @Test
    void getReinforcement() {
        StarSystemData firstSystem = StarSystemData.builder().starSystemId(SYSTEM_ID).build();
        StarSystemData secondSystem = StarSystemData.builder().starSystemId(UUID.randomUUID()).build();
        Station firstStation = Station.builder()
            .id(STATION_ID_1)
            .starSystemId(SYSTEM_ID)
            .stationName(STATION_NAME_1)
            .build();
        Station secondStation = Station.builder()
            .id(STATION_ID_2)
            .starSystemId(SYSTEM_ID)
            .stationName(STATION_NAME_2)
            .build();
        Commodity firstCommodity = Commodity.builder()
            .externalReference(STATION_ID_1)
            .itemName(COMMODITY_NAME_1)
            .demand(DEMAND_1)
            .buyPrice(PRICE_1)
            .build();
        Commodity secondCommodity = Commodity.builder()
            .externalReference(STATION_ID_2)
            .itemName(COMMODITY_NAME_2)
            .demand(DEMAND_2)
            .buyPrice(PRICE_2)
            .build();
        LastUpdate firstLastUpdate = LastUpdate.builder().externalReference(STATION_ID_1).type(ItemType.COMMODITY).lastUpdate(LAST_UPDATE_1).build();
        LastUpdate secondLastUpdate = LastUpdate.builder().externalReference(STATION_ID_2).type(ItemType.COMMODITY).lastUpdate(LAST_UPDATE_2).build();

        given(metallicRingService.getSystemsWithMetallicRing(List.of(firstSystem.getStarSystemId(), secondSystem.getStarSystemId()))).willReturn(Map.of(SYSTEM_ID, ReserveLevel.MAJOR));
        given(stationDao.getByStarSystemIds(eq(Set.of(SYSTEM_ID)))).willReturn(List.of(firstStation, secondStation));
        given(offerService.getOffers(List.of(firstStation, secondStation), PowerplayActivityType.REINFORCEMENT)).willReturn(List.of(firstCommodity, secondCommodity));
        given(starSystemDao.getByIds(List.of(SYSTEM_ID))).willReturn(List.of(StarSystem.builder().id(SYSTEM_ID).starName(SYSTEM_NAME).build()));
        given(lastUpdateDao.findByIdValidated(STATION_ID_1, ItemType.COMMODITY)).willReturn(firstLastUpdate);
        given(lastUpdateDao.findByIdValidated(STATION_ID_2, ItemType.COMMODITY)).willReturn(secondLastUpdate);

        List<MiningMeritFarmResponse> result = underTest.getReinforcement(List.of(firstSystem, secondSystem));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(MiningMeritFarmResponse::getSourceStarSystemId).containsExactly(SYSTEM_ID, SYSTEM_ID);
        assertThat(result).extracting(MiningMeritFarmResponse::getSourceStarSystemName).containsExactly(SYSTEM_NAME, SYSTEM_NAME);
        assertThat(result).extracting(MiningMeritFarmResponse::getTargetStarSystemId).containsExactly(SYSTEM_ID, SYSTEM_ID);
        assertThat(result).extracting(MiningMeritFarmResponse::getTargetStarSystemName).containsExactly(SYSTEM_NAME, SYSTEM_NAME);
        assertThat(result).extracting(MiningMeritFarmResponse::getReserveLevel).containsExactly(ReserveLevel.MAJOR, ReserveLevel.MAJOR);
        assertThat(result).extracting(MiningMeritFarmResponse::getStationId).containsExactly(STATION_ID_1, STATION_ID_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getStationName).containsExactly(STATION_NAME_1, STATION_NAME_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getCommodityName).containsExactly(COMMODITY_NAME_1, COMMODITY_NAME_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getDemand).containsExactly(DEMAND_1, DEMAND_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getPrice).containsExactly(PRICE_1, PRICE_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getLastUpdate).containsExactly(LAST_UPDATE_1, LAST_UPDATE_2);
        assertThat(result).extracting(MiningMeritFarmResponse::getActivityType).containsOnly(PowerplayActivityType.REINFORCEMENT);
    }
}