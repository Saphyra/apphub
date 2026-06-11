package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.StationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ReinforcementOfferCollector {
    private final MetallicRingService metallicRingService;
    private final StationDao stationDao;
    private final OfferService offerService;
    private final StarSystemDao starSystemDao;
    private final LastUpdateDao lastUpdateDao;

    List<MiningMeritFarmResponse> getReinforcement(List<StarSystemData> starSystemData) {
        log.info("{} systems found for reinforcement", starSystemData.size());
        Map<UUID, ReserveLevel> starSystemsWithMetallicRing = metallicRingService.getSystemsWithMetallicRing(starSystemData.stream().map(StarSystemData::getStarSystemId).toList());
        log.info("{} reinforcement systems have metallic ring", starSystemsWithMetallicRing.size());

        List<Station> stations = stationDao.getByStarSystemIds(starSystemsWithMetallicRing.keySet());
        log.info("{} stations found in reinforcement systems", stations.size());

        Map<UUID, Station> stationMap = stations.stream()
            .collect(Collectors.toMap(Station::getId, station -> station));

        List<Commodity> offers = offerService.getOffers(stations, PowerplayActivityType.REINFORCEMENT);

        List<UUID> starSystemIds = offers.stream()
            .map(commodity -> stationMap.get(commodity.getExternalReference()).getStarSystemId())
            .distinct()
            .toList();

        Map<UUID, String> starSystemNames = starSystemDao.getByIds(starSystemIds)
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, StarSystem::getStarName));

        return offers.stream()
            .map(offer -> {
                UUID starSystemId = stationMap.get(offer.getExternalReference())
                    .getStarSystemId();
                return MiningMeritFarmResponse.builder()
                    .sourceStarSystemId(starSystemId)
                    .sourceStarSystemName(starSystemNames.get(starSystemId))
                    .reserveLevel(starSystemsWithMetallicRing.get(starSystemId))
                    .targetStarSystemId(starSystemId)
                    .targetStarSystemName(starSystemNames.get(starSystemId))
                    .stationId(offer.getExternalReference())
                    .stationName(stationMap.get(offer.getExternalReference()).getStationName())
                    .commodityName(offer.getItemName())
                    .demand(offer.getDemand())
                    .price(offer.getBuyPrice())
                    .lastUpdate(lastUpdateDao.findByIdValidated(offer.getExternalReference(), ItemType.COMMODITY).getLastUpdate())
                    .activityType(PowerplayActivityType.REINFORCEMENT)
                    .build();
            })
            .toList();
    }
}
