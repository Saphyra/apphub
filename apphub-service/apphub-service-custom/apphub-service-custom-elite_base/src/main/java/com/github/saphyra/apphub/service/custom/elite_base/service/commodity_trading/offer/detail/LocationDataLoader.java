package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.Station;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
class LocationDataLoader {
    private final ExecutorServiceBean executorServiceBean;
    private final StationDao stationDao;
    private final FleetCarrierDao fleetCarrierDao;

    Map<UUID, ItemLocationData> loadLocationData(List<Offer> offers) {
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
