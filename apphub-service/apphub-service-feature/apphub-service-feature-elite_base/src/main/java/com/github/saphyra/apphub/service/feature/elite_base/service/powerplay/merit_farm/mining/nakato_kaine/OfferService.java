package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm.PowerplayActivityType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.CommodityDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.station.Station;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class OfferService {
    private static final List<String> COMMODITIES = List.of("painite", "platinum");

    private final CommodityDao commodityDao;
    private final ExecutorServiceBean executorServiceBean;

    List<Commodity> getOffers(List<Station> stations, PowerplayActivityType activityType) {
        Map<UUID, List<Station>> stationsOfStarSystems = stations.stream()
            .collect(Collectors.groupingBy(Station::getStarSystemId));

        List<BiWrapper<UUID, String>> stationIdCommodityMapping = stationsOfStarSystems.values()
            .stream()
            .flatMap(Collection::stream)
            .map(Station::getId)
            .flatMap(stationId -> COMMODITIES.stream().map(commodity -> new BiWrapper<>(stationId, commodity)))
            .toList();
        log.info("Searching for {} offers for {} systems", stationIdCommodityMapping.size(), activityType);

        List<FutureWrapper<List<Commodity>>> futures = Lists.partition(stationIdCommodityMapping, 1000)
            .stream()
            .map(batch -> executorServiceBean.asyncProcess(() -> commodityDao.getByIds(batch)))
            .toList();

        List<Commodity> offers = futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .flatMap(Collection::stream)
            .filter(commodity -> commodity.getDemand() > 0)
            .toList();
        log.info("{} offers found for {} systems", offers.size(), activityType);
        return offers;
    }
}
