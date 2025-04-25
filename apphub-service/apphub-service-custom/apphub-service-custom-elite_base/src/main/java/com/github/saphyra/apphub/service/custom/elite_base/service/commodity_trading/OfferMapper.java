package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingResponse;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class OfferMapper {
    private final NDimensionDistanceCalculator distanceCalculator;
    private final LastUpdateDao lastUpdateDao;

    Optional<CommodityTradingResponse> mapOffer(
        TradeMode tradeMode,
        StarSystem referenceSystem,
        Map<UUID, CommodityLocationData> commodityLocationDatas,
        Map<UUID, StarSystem> stars,
        Map<UUID, StarSystemData> systemDatas,
        Map<UUID, Body> bodies,
        Commodity commodity
    ) {
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
            .locationType(Optional.ofNullable(commodityLocationData.getStationType()).map(Enum::name).orElse(null))
            .stationDistance(stationDistance)
            .landingPad(Optional.ofNullable(commodityLocationData.getStationType()).map(StationType::getLandingPad).orElse(null))
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
}
