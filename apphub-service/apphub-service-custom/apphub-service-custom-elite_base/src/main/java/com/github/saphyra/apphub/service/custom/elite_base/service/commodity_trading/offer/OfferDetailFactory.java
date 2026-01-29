package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMapping;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power.StarSystemPowerMappingDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
class OfferDetailFactory {
    private final ExecutorServiceBean executorServiceBean;
    private final StationDao stationDao;
    private final FleetCarrierDao fleetCarrierDao;
    private final StarSystemDataDao starSystemDataDao;
    private final StarSystemPowerMappingDao starSystemPowerMappingDao;
    private final BodyDao bodyDao;

    List<OfferDetail> create(List<Offer> offers) {
        FutureWrapper<Map<UUID, ItemLocationData>> locationDataMap = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> loadStations(offers),
            "Station query took {} ms"
        ));
        FutureWrapper<Map<UUID, StarSystemData>> starSystemDataMap = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> loadStarSystemData(offers),
            "StarSystemData query took {} ms"
        ));
        FutureWrapper<Map<UUID, List<Power>>> powers = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> loadPowers(offers),
            "Power query took {} ms"
        ));
        List<UUID> bodyIds = locationDataMap.get().getOrThrow().values().stream().map(ItemLocationData::getBodyId).filter(Objects::nonNull).toList();
        Map<UUID, Body> bodies = Utils.measuredOperation(
            () -> loadBodies(bodyIds),
            "Body query took {} ms"
        );

        return offers.stream()
            .map(offer -> {
                ItemLocationData locationData = locationDataMap.get()
                    .getOrThrow()
                    .get(offer.getExternalReference());

                return OfferDetail.builder()
                    .offer(offer)
                    .locationData(locationData)
                    .starSystemData(starSystemDataMap.get().getOrThrow().get(offer.getStarSystemId()))
                    .powers(powers.get().getOrThrow().get(offer.getStarSystemId()))
                    .body(Optional.ofNullable(locationData.getBodyId()).map(bodies::get).orElse(null))
                    .build();
            })
            .toList();
    }

    private Map<UUID, Body> loadBodies(Collection<UUID> locationIds) {
        return bodyDao.findAllById(locationIds)
            .stream()
            .collect(Collectors.toMap(Body::getId, body -> body));
    }

    private Map<UUID, List<Power>> loadPowers(List<Offer> offers) {
        List<UUID> starSystemIds = getStarSystemIds(offers);

        return starSystemPowerMappingDao.getByStarSystemIds(starSystemIds)
            .stream()
            .collect(Collectors.groupingBy(StarSystemPowerMapping::getStarSystemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(StarSystemPowerMapping::getPower).toList()));
    }

    private Map<UUID, StarSystemData> loadStarSystemData(List<Offer> offers) {
        List<UUID> starSystemIds = getStarSystemIds(offers);

        return starSystemDataDao.findAllById(starSystemIds)
            .stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, starSystemData -> starSystemData));
    }

    private static List<UUID> getStarSystemIds(List<Offer> offers) {
        return offers.stream()
            .map(Offer::getStarSystemId)
            .distinct()
            .toList();
    }

    private Map<UUID, ItemLocationData> loadStations(List<Offer> offers) {
        Map<ItemLocationType, List<UUID>> idsByType = offers.stream()
            .collect(Collectors.groupingBy(Offer::getLocationType))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(Offer::getExternalReference).toList()));

        FutureWrapper<List<Station>> stations = executorServiceBean.asyncProcess(
            () -> Optional.ofNullable(idsByType.get(ItemLocationType.STATION))
                .map(stationDao::findAllById)
                .orElseGet(List::of)
        );

        List<FleetCarrier> fleetCarriers = Optional.ofNullable(idsByType.get(ItemLocationType.FLEET_CARRIER))
            .map(fleetCarrierDao::findAllById)
            .orElseGet(List::of);

        return Stream.concat(
                stations.get().getOrThrow().stream(),
                fleetCarriers.stream()
            )
            .collect(Collectors.toMap(
                ItemLocationData::getId,
                itemLocationData -> itemLocationData
            ));
    }
}
