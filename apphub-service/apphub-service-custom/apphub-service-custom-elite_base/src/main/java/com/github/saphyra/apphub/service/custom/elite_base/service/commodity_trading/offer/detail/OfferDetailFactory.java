package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.detail;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.Offer;
import com.github.saphyra.apphub.service.custom.elite_base.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfferDetailFactory {
    private final ExecutorServiceBean executorServiceBean;
    private final LocationDataLoader locationDataLoader;
    private final StarSystemDataLoader starSystemDataLoader;
    private final BodyDao bodyDao;

    public List<OfferDetail> create(List<Offer> offers) {
        FutureWrapper<Map<UUID, ItemLocationData>> locationDataMap = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> locationDataLoader.loadLocationData(offers),
            "Station query took {} ms"
        ));
        FutureWrapper<Map<UUID, StarSystemData>> starSystemDataMap = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> starSystemDataLoader.loadStarSystemData(offers),
            "StarSystemData query took {} ms"
        ));
        FutureWrapper<Map<UUID, List<Power>>> powers = executorServiceBean.asyncProcess(() -> Utils.measuredOperation(
            () -> starSystemDataLoader.loadPowers(offers),
            "Power query took {} ms"
        ));
        List<UUID> bodyIds = locationDataMap.get()
            .getOrThrow()
            .values()
            .stream()
            .map(ItemLocationData::getBodyId)
            .filter(Objects::nonNull)
            .toList();
        Map<UUID, Body> bodies = Utils.measuredOperation(
            () -> loadBodies(bodyIds),
            "Body query took {} ms"
        );

        return offers.stream()
            .map(offer -> {
                ItemLocationData locationData = locationDataMap.get()
                    .getOrThrow()
                    .get(offer.getExternalReference());

                UUID bodyId = locationData.getBodyId();
                return OfferDetail.builder()
                    .offer(offer)
                    .locationData(locationData)
                    .starSystemData(starSystemDataMap.get().getOrThrow().get(offer.getStarSystemId()))
                    .powers(powers.get().getOrThrow().get(offer.getStarSystemId()))
                    .body(Optional.ofNullable(bodyId).map(bodies::get).orElse(null))
                    .build();
            })
            .toList();
    }

    private Map<UUID, Body> loadBodies(Collection<UUID> locationIds) {
        return bodyDao.findAllById(locationIds)
            .stream()
            .collect(Collectors.toMap(Body::getId, body -> body));
    }
}
