package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.MiningMeritFarmResponse;
import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
class AcquisitionOfferCollector {
    private final MetallicRingService metallicRingService;
    private final StarSystemDao starSystemDao;
    private final StationDao stationDao;
    private final OfferService offerService;
    private final LastUpdateDao lastUpdateDao;
    private final AcquisitionTargetProvider acquisitionTargetProvider;

    List<MiningMeritFarmResponse> getAcquisition(List<StarSystemData> sourceSystemCandidates) {
        log.info("There are {} source systems for acquisition", sourceSystemCandidates.size());
        Map<UUID, ReserveLevel> sourceSystemIds = metallicRingService.getSystemsWithMetallicRing(sourceSystemCandidates.stream().map(StarSystemData::getStarSystemId).toList());
        log.info("There are {} source systems for acquisition with metallic ring", sourceSystemIds.size());

        Map<UUID, StarSystem> sourceSystems = starSystemDao.getByIds(sourceSystemIds.keySet())
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, starSystem -> starSystem));

        Map<UUID, List<StarSystem>> targetsSourceMapping = acquisitionTargetProvider.getAcquisitionTargets(sourceSystems.values(), sourceSystemCandidates);

        List<UUID> targetStarSystemIds = targetsSourceMapping.values()
            .stream()
            .flatMap(Collection::stream)
            .map(StarSystem::getId)
            .distinct()
            .toList();
        log.info("There are {} unoccupied systems available for acquisition", targetStarSystemIds.size());

        List<Station> stations = stationDao.getByStarSystemIds(targetStarSystemIds);
        log.info("There are {} stations in acquirable systems", stations.size());

        List<Commodity> offers = offerService.getOffers(stations, PowerplayActivityType.ACQUISITION);

        Map<UUID, String> starSystemNames = Stream.concat(
                sourceSystems.values().stream(),
                targetsSourceMapping.values().stream().flatMap(Collection::stream)
            )
            .map(starSystem -> new BiWrapper<>(starSystem.getId(), starSystem.getStarName()))
            .distinct()
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));

        Map<UUID, Station> stationMap = stations.stream()
            .collect(Collectors.toMap(Station::getId, station -> station));

        return targetsSourceMapping.entrySet()
            .stream()
            .flatMap(entry -> entry.getValue().stream().map(targetSystem -> new BiWrapper<>(entry.getKey(), targetSystem.getId())))
            .flatMap(route -> {
                UUID sourceSystemId = route.getEntity1();
                UUID targetSystemId = route.getEntity2();

                return offers.stream()
                    .filter(commodity -> stationMap.get(commodity.getExternalReference()).getStarSystemId().equals(targetSystemId))
                    .map(offer -> new TriWrapper<>(sourceSystemId, targetSystemId, offer));
            })
            .map(tw -> {
                UUID sourceSystemId = tw.getEntity1();
                UUID targetSystemId = tw.getEntity2();
                Commodity offer = tw.getEntity3();

                return MiningMeritFarmResponse.builder()
                    .sourceStarSystemId(sourceSystemId)
                    .sourceStarSystemName(starSystemNames.get(sourceSystemId))
                    .reserveLevel(sourceSystemIds.get(sourceSystemId))
                    .targetStarSystemId(targetSystemId)
                    .targetStarSystemName(starSystemNames.get(targetSystemId))
                    .stationId(offer.getExternalReference())
                    .stationName(stationMap.get(offer.getExternalReference()).getStationName())
                    .commodityName(offer.getItemName())
                    .demand(offer.getDemand())
                    .price(offer.getBuyPrice())
                    .lastUpdate(lastUpdateDao.findByIdOrDefault(offer.getExternalReference(), ObjectType.COMMODITY).getLastUpdate())
                    .activityType(PowerplayActivityType.ACQUISITION)
                    .build();
            })
            .toList();
    }
}
