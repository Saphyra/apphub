package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.station.StationDao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO split
public class CommodityTradingService {
    private final CommodityTradingRequestValidator commodityTradingRequestValidator;
    private final CommodityDao commodityDao;
    private final StarSystemDao starSystemDao;
    private final StationDao stationDao;
    private final StarSystemDataDao starSystemDataDao;
    private final BodyDao bodyDao;
    private final NDimensionDistanceCalculator distanceCalculator;
    private final FleetCarrierDao fleetCarrierDao;
    private final LastUpdateDao lastUpdateDao;
    private final DateTimeUtil dateTimeUtil;

    public List<CommodityTradingResponse> getTradeOffers(CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);
        TradeMode tradeMode = ValidationUtil.convertToEnumChecked(request.getTradeMode(), TradeMode::valueOf, "tradeMode");

        StarSystem referenceSystem = starSystemDao.findByIdValidated(request.getReferenceStarId());

        List<Commodity> offers = tradeMode.getOfferProvider().apply(commodityDao, request);
        log.info("Offers found: {}", offers.size());

        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(request.getMaxTimeSinceLastUpdated());

        List<CommodityTradingResponse> result = assembleResponses(tradeMode, referenceSystem, offers, request.getIncludeFleetCarriers())
            .stream()
            .filter(systemDistanceFilter(request))
            .filter(stationDistanceFilter(request))
            .filter(landingPadFilter(request))
            .filter(lastUpdateFilter(expiration))
            .filter(surfaceStationFilter(request))
            .filter(controllingFactionFilter(request))
            .filter(powerplayStateFilter(request))
            .filter(tradeAmountFilter(request))
            .toList();
        log.info("Remaining offers after filtering: {}", result.size());
        return result;
    }

    private static Predicate<CommodityTradingResponse> tradeAmountFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            boolean result = commodityTradingResponse.getTradeAmount() >= request.getMinTradeAmount();
            if (!result) {
                log.info("Filtering offer with amount too low {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private static Predicate<CommodityTradingResponse> powerplayStateFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (isNull(request.getPowerplayState())) {
                return true;
            }

            boolean result = Optional.ofNullable(commodityTradingResponse.getPowerplayState())
                .filter(powerplayState -> powerplayState.equals(request.getPowerplayState()))
                .isPresent();
            if (!result) {
                log.info("Filtering offer from system with incorrect powerplayState {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private static Predicate<CommodityTradingResponse> controllingFactionFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            boolean result = request.getPowersRelation().apply(request.getControllingPowers(), Optional.ofNullable(commodityTradingResponse.getControllingPower()).map(List::of).orElse(Collections.emptyList()));
            if (!result) {
                log.info("Filtered offer with incorrect controllingFaction: {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private static Predicate<CommodityTradingResponse> systemDistanceFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            boolean result = commodityTradingResponse.getStarSystemDistance() <= request.getMaxStarSystemDistance();
            if (!result) {
                log.info("Filtered offer because system is too far: {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private Predicate<CommodityTradingResponse> surfaceStationFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (request.getIncludeSurfaceStations()) {
                return true;
            }

            StationType stationType = StationType.valueOf(commodityTradingResponse.getLocationType());

            boolean result = stationType != StationType.SURFACE_STATION && stationType != StationType.ON_FOOT_SETTLEMENT;
            if (!result) {
                log.info("Filtered offer at surface station: {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private Predicate<CommodityTradingResponse> lastUpdateFilter(LocalDateTime expiration) {
        return commodityTradingResponse -> {
            boolean result = commodityTradingResponse.getLastUpdated().isAfter(expiration);
            if (!result) {
                log.info("Filtered offer based on expired data. Expiration: {}. {}", expiration, commodityTradingResponse);
            }
            return result;
        };
    }

    private Predicate<CommodityTradingResponse> landingPadFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (isNull(commodityTradingResponse.getLandingPad())) {
                boolean result = request.getIncludeUnknownLandingPad();
                if (!result) {
                    log.info("Filtered offer from station with unknown landing pad: {}", commodityTradingResponse);
                }
                return result;
            }

            boolean result = commodityTradingResponse.getLandingPad().isLargeEnough(request.getMinLandingPad());
            if (!result) {
                log.info("Filtered offer from station with too small landing pad: {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private static Predicate<CommodityTradingResponse> stationDistanceFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (isNull(commodityTradingResponse.getStationDistance())) {
                Boolean result = request.getIncludeUnknownStationDistance();
                if (!result) {
                    log.info("Filtered offer from station with unknown distance: {}", commodityTradingResponse);
                }
                return result;
            }

            boolean result = commodityTradingResponse.getStationDistance() <= request.getMaxStationDistance();
            if (!result) {
                log.info("Filtered offer from too far station: {}", commodityTradingResponse);
            }
            return result;
        };
    }

    private List<CommodityTradingResponse> assembleResponses(TradeMode tradeMode, StarSystem referenceSystem, List<Commodity> offers, boolean includeFleetCarriers) {
        Map<UUID, CommodityLocationData> commodityLocationDatas = new HashMap<>();

        //Fetch stations for commodities
        List<UUID> locationIds = offers.stream()
            .map(Commodity::getExternalReference)
            .toList();
        stationDao.findAllById(locationIds)
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
            fleetCarrierDao.findAllById(locationIds)
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
        Map<UUID, StarSystem> stars = starSystemDao.findAllById(starIds)
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, Function.identity()));

        //Fetch starSystemData for Powerplay data
        Map<UUID, StarSystemData> systemDatas = starSystemDataDao.findAllById(starIds)
            .stream()
            .collect(Collectors.toMap(StarSystemData::getStarSystemId, Function.identity()));

        //Fetch bodies for stationDistance
        List<UUID> bodyIds = commodityLocationDatas.values()
            .stream()
            .map(CommodityLocationData::getBodyId)
            .filter(Objects::nonNull)
            .toList();
        Map<UUID, Body> bodies = bodyDao.findAllById(bodyIds)
            .stream()
            .collect(Collectors.toMap(Body::getId, Function.identity()));

        //Convert fetched data to response
        return offers.stream()
            .map(commodity -> mapCommodities(tradeMode, referenceSystem, commodityLocationDatas, stars, systemDatas, bodies, commodity))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    private Optional<CommodityTradingResponse> mapCommodities(TradeMode tradeMode, StarSystem referenceSystem, Map<UUID, CommodityLocationData> commodityLocationDatas, Map<UUID, StarSystem> stars, Map<UUID, StarSystemData> systemDatas, Map<UUID, Body> bodies, Commodity commodity) {
        CommodityLocationData commodityLocationData = commodityLocationDatas.get(commodity.getExternalReference());
        if (isNull(commodityLocationData)) {
            log.info("CommodityLocationData not found for externalReference {}", commodity.getExternalReference());
            return Optional.empty();
        }

        StarSystem starSystem = stars.get(commodityLocationData.getStarSystemId());
        if (isNull(starSystem)) {
            log.info("StarSystem not found for {}", commodityLocationData);
            return Optional.empty();
        }

        Double stationDistance = Optional.ofNullable(commodityLocationData.getBodyId())
            .map(bodies::get)
            .map(Body::getDistanceFromStar)
            .orElse(null);
        StarSystemData starSystemData = systemDatas.get(starSystem.getId());
        Double distanceFromStar = getDistanceFromStar(referenceSystem.getPosition(), starSystem.getPosition());

        CommodityTradingResponse result = CommodityTradingResponse.builder()
            .starId(starSystem.getId())
            .starName(starSystem.getStarName())
            .starSystemDistance(distanceFromStar)
            .externalReference(commodityLocationData.getExternalReference())
            .locationName(commodityLocationData.getLocationName())
            .locationType(commodityLocationData.getStationType().name())
            .stationDistance(stationDistance)
            .landingPad(commodityLocationData.getStationType().getLandingPad())
            .tradeAmount(tradeMode.getTradeAmountExtractor().apply(commodity))
            .price(tradeMode.getPriceExtractor().apply(commodity))
            .controllingPower(Optional.ofNullable(starSystemData).map(StarSystemData::getControllingPower).map(Enum::name).orElse(null))
            .powers(Optional.ofNullable(starSystemData).map(StarSystemData::getPowers).map(powers -> powers.stream().map(Enum::name).toList()).orElse(null))
            .powerplayState(Optional.ofNullable(starSystemData).map(StarSystemData::getPowerplayState).map(Enum::name).orElse(null))
            .lastUpdated(lastUpdateDao.findByIdValidated(commodityLocationData.getExternalReference(), commodity.getType().get()).getLastUpdate()) //LastUpdateDao is cached
            .build();
        return Optional.of(result);
    }

    private Double getDistanceFromStar(StarSystemPosition referencePosition, StarSystemPosition position) {
        if (!referencePosition.isFilled() || !position.isFilled()) {
            return 100_000D;
        }

        return distanceCalculator.calculateDistance(
            new NDimensionCoordinate(referencePosition.getX(), referencePosition.getY(), referencePosition.getZ()),
            new NDimensionCoordinate(position.getX(), position.getY(), position.getZ())
        );
    }

    @AllArgsConstructor
    @Data
    @Builder
    private static class CommodityLocationData {
        private final UUID externalReference;
        private final UUID starSystemId;
        private final UUID bodyId;
        private final String locationName;
        @Builder.Default
        private final StationType stationType = StationType.FLEET_CARRIER;
    }
}
