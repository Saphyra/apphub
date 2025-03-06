package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
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

    public List<CommodityTradingResponse> getTradeOffers(TradeMode tradeMode, CommodityTradingRequest request) {
        commodityTradingRequestValidator.validate(request);

        StarSystem referenceSystem = starSystemDao.findByIdValidated(request.getReferenceStarId());

        List<Commodity> offers = tradeMode.getOfferProvider().apply(commodityDao, request);

        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(request.getMaxTimeSinceLastUpdated());

        return assembleResponses(tradeMode, referenceSystem, offers, request.getIncludeFleetCarriers())
            .stream()
            .filter(commodityTradingResponse -> commodityTradingResponse.getStarSystemDistance() <= request.getMaxStarSystemDistance())
            .filter(stationDistanceFilter(request))
            .filter(landingPadFilter(request))
            .filter(lastUpdateFilter(expiration))
            .filter(surfaceStationFilter(request))
            .filter(commodityTradingResponse -> request.getPowerRelation().apply(request.getControllingPowers(), List.of(commodityTradingResponse.getControllingPower())))
            .filter(commodityTradingResponse -> isNull(request.getPowerplayState()) || commodityTradingResponse.getPowerplayState().equals(request.getPowerplayState()))
            .filter(commodityTradingResponse -> commodityTradingResponse.getTradeAmount() >= request.getMinTradeAmount())
            .toList();
    }

    private Predicate<CommodityTradingResponse> surfaceStationFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (request.getIncludeSurfaceStations()) {
                return true;
            }

            StationType stationType = StationType.valueOf(commodityTradingResponse.getLocationType());

            return stationType != StationType.SURFACE_STATION && stationType != StationType.ON_FOOT_SETTLEMENT;
        };
    }

    private Predicate<CommodityTradingResponse> lastUpdateFilter(LocalDateTime expiration) {
        return commodityTradingResponse -> commodityTradingResponse.getLastUpdated().isAfter(expiration);
    }

    private Predicate<CommodityTradingResponse> landingPadFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (isNull(commodityTradingResponse.getLandingPad()) && !request.getIncludeUnknownLandingPad()) {
                return false;
            }

            return commodityTradingResponse.getLandingPad().isLargeEnough(request.getMinLandingPad());
        };
    }

    private static Predicate<CommodityTradingResponse> stationDistanceFilter(CommodityTradingRequest request) {
        return commodityTradingResponse -> {
            if (isNull(commodityTradingResponse.getStationDistance()) && !request.getIncludeUnknownStationDistance()) {
                return false;
            }

            return commodityTradingResponse.getStationDistance() <= request.getMaxStationDistance();
        };
    }

    private List<CommodityTradingResponse> assembleResponses(TradeMode tradeMode, StarSystem referenceSystem, List<Commodity> offers, boolean includeFleetCarriers) {
        Map<UUID, CommodityLocationData> commodityLocationDatas = new HashMap<>();

        //Fetch stations for commodities
        List<UUID> stationIds = offers.stream()
            .filter(commodity -> commodity.getCommodityLocation() == CommodityLocation.STATION)
            .map(Commodity::getExternalReference)
            .toList();
        stationDao.findAllById(stationIds)
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
            List<UUID> fleetCarrierIds = offers.stream()
                .filter(commodity -> commodity.getCommodityLocation() == CommodityLocation.FLEET_CARRIER)
                .map(Commodity::getExternalReference)
                .toList();

            fleetCarrierDao.findAllById(fleetCarrierIds)
                .stream()
                .map(fleetCarrier -> CommodityLocationData.builder()
                    .externalReference(fleetCarrier.getId())
                    .starSystemId(fleetCarrier.getStarSystemId())
                    .locationName(fleetCarrier.getCarrierName())
                    .build())
                .forEach(commodityLocationData -> commodityLocationDatas.put(commodityLocationData.getExternalReference(), commodityLocationData));
        }

        //Fetch starSystems
        List<UUID> starIds = commodityLocationDatas.values()
            .stream()
            .map(CommodityLocationData::getStarSystemId)
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
            .map(commodity -> {
                CommodityLocationData commodityLocationData = commodityLocationDatas.get(commodity.getExternalReference());
                StarSystem starSystem = stars.get(commodityLocationData.getStarSystemId());
                Double stationDistance = Optional.ofNullable(commodityLocationData.getBodyId())
                    .map(bodies::get)
                    .map(Body::getDistanceFromStar)
                    .orElse(null);
                StarSystemData starSystemData = systemDatas.get(starSystem.getId());
                return CommodityTradingResponse.builder()
                    .starId(starSystem.getId())
                    .starName(starSystem.getStarName())
                    .starSystemDistance(getDistanceFromStar(referenceSystem.getPosition(), starSystem.getPosition()))
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
            })
            .toList();
    }

    private double getDistanceFromStar(StarSystemPosition referencePosition, StarSystemPosition position) {
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
