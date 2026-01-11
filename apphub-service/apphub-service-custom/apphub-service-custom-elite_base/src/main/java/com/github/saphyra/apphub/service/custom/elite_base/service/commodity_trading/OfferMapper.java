package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionCoordinate;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
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
//TODO unit test
class OfferMapper {
    private final NDimensionDistanceCalculator distanceCalculator;

    Optional<OfferDetail> mapOffer(
        TradeMode tradeMode,
        StarSystem referenceSystem,
        Map<UUID, CommodityLocationData> commodityLocationDatas,
        Map<UUID, StarSystem> stars,
        Map<UUID, StarSystemData> systemDatas,
        Map<UUID, Body> bodies,
        Tradeable commodity
    ) {
        CommodityLocationData commodityLocationData = commodityLocationDatas.get(commodity.getExternalReference());
        if (isNull(commodityLocationData)) {
            log.debug("CommodityLocationData not found for externalReference {}", commodity.getExternalReference());
            return Optional.empty();
        }

        StarSystem starSystem = stars.get(commodityLocationData.getStarSystemId());
        if (isNull(starSystem)) {
            log.info("StarSystem not found for {}", commodityLocationData);
            return Optional.empty();
        }

        StarSystemData starSystemData = systemDatas.get(starSystem.getId());
        Double distanceFromReferenceStar = getDistanceFromStar(referenceSystem.getPosition(), starSystem.getPosition());

        return Optional.of(OfferDetail.builder()
            .tradeMode(tradeMode)
            .starSystem(starSystem)
            .distanceFromReferenceSystem(distanceFromReferenceStar)
            .commodityLocationData(commodityLocationData)
            .body(Optional.ofNullable(commodityLocationData.getBodyId()).map(bodies::get).orElse(null))
            .tradingItem(commodity)
            .starSystemData(starSystemData)
            .build());
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
