package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class OfferDetailsFetcher {
    private final StarSystemDao starSystemDao;
    private final StationDao stationDao;
    private final StarSystemDataDao starSystemDataDao;
    private final BodyDao bodyDao;
    private final FleetCarrierDao fleetCarrierDao;
    private final OfferMapper offerMapper;
    private final PerformanceReporter performanceReporter;

    List<CommodityTradingResponse> assembleResponses(TradeMode tradeMode, StarSystem referenceSystem, List<Commodity> offers, boolean includeFleetCarriers) {
        Map<UUID, CommodityLocationData> commodityLocationDatas = new HashMap<>();

        //Fetch stations for commodities
        List<UUID> locationIds = offers.stream()
            .map(Commodity::getExternalReference)
            .toList();
        performanceReporter.wrap(
                () -> stationDao.findAllById(locationIds),
                PerformanceReportingTopic.ELITE_BASE_QUERY,
                PerformanceReportingKey.COMMODITY_TRADING_GET_STATIONS.name()
            )
            .stream()
            .map(station -> CommodityLocationData.builder()
                .externalReference(station.getId())
                .starSystemId(station.getStarSystemId())
                .bodyId(station.getBodyId())
                .locationName(station.getStationName())
                .stationType(station.getType())
                .build())
            .forEach(commodityLocationData -> commodityLocationDatas.put(commodityLocationData.getExternalReference(), commodityLocationData));

        //Fetch Fleet carriers for commodities, if client requests it
        if (includeFleetCarriers) {
            performanceReporter.wrap(
                    () -> fleetCarrierDao.findAllById(locationIds),
                    PerformanceReportingTopic.ELITE_BASE_QUERY,
                    PerformanceReportingKey.COMMODITY_TRADING_GET_FLEET_CARRIERS.name()
                )
                .stream()
                .map(fleetCarrier -> CommodityLocationData.builder()
                    .externalReference(fleetCarrier.getId())
                    .starSystemId(fleetCarrier.getStarSystemId())
                    .locationName(String.join(" - ", fleetCarrier.getCarrierId(), fleetCarrier.getCarrierName()))
                    .build())
                .forEach(commodityLocationData -> commodityLocationDatas.put(commodityLocationData.getExternalReference(), commodityLocationData));
        }

        //Fetch starSystems
        List<UUID> starIds = commodityLocationDatas.values()
            .stream()
            .map(CommodityLocationData::getStarSystemId)
            .filter(Objects::nonNull)
            .toList();
        Map<UUID, StarSystem> stars = performanceReporter.wrap(
                () -> starSystemDao.findAllById(starIds),
                PerformanceReportingTopic.ELITE_BASE_QUERY,
                PerformanceReportingKey.COMMODITY_TRADING_GET_STAR_SYSTEMS.name()
            )
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, Function.identity()));

        //Fetch starSystemData for Powerplay data
        Map<UUID, StarSystemData> systemDatas = performanceReporter.wrap(
                () -> starSystemDataDao.findAllById(starIds),
                PerformanceReportingTopic.ELITE_BASE_QUERY,
                PerformanceReportingKey.COMMODITY_TRADING_GET_STAR_SYSTEM_DATA.name()
            )
            .stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, Function.identity()));

        //Fetch bodies for stationDistance
        List<UUID> bodyIds = commodityLocationDatas.values()
            .stream()
            .map(CommodityLocationData::getBodyId)
            .filter(Objects::nonNull)
            .toList();
        Map<UUID, Body> bodies = performanceReporter.wrap(
            () -> bodyDao.findAllById(bodyIds),
            PerformanceReportingTopic.ELITE_BASE_QUERY,
            PerformanceReportingKey.COMMODITY_TRADING_GET_BODIES.name()
            )
            .stream()
            .collect(Collectors.toMap(Body::getId, Function.identity()));

        //Convert fetched data to response
        return offers.stream()
            .map(commodity -> offerMapper.mapOffer(tradeMode, referenceSystem, commodityLocationDatas, stars, systemDatas, bodies, commodity))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
}
