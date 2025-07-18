package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.api.custom.elite_base.model.CommodityTradingRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CommodityTradingRequestValidator {
    private final CommodityDao commodityDao;

    void validate(CommodityTradingRequest request) {
        ValidationUtil.notNull(request.getReferenceStarId(), "referenceStarId");
        ValidationUtil.contains(request.getCommodity(), commodityDao.getCommodityNames(), "commodity");
        ValidationUtil.notNull(request.getMaxStarSystemDistance(), "maxStarSystemDistance");
        ValidationUtil.notNull(request.getMaxStationDistance(), "maxStationDistance");
        ValidationUtil.notNull(request.getIncludeUnknownStationDistance(), "includeUnknownStationDistance");
        ValidationUtil.notNull(request.getIncludeUnknownLandingPad(), "includeUnknownLandingPad");
        ValidationUtil.notNull(request.getMaxTimeSinceLastUpdated(), "maxTimeSinceLastUpdated");
        ValidationUtil.notNull(request.getIncludeSurfaceStations(), "includeSurfaceStations");
        ValidationUtil.notNull(request.getIncludeFleetCarriers(), "includeFleetCarriers");
        ValidationUtil.atLeast(request.getMinPrice(), 1, "minPrice");
        ValidationUtil.atLeast(request.getMaxPrice(), request.getMinPrice(), "maxPrice");

        ValidationUtil.notNull(request.getControllingPowers(), "controllingPowers");
        if (!request.getControllingPowers().isEmpty()) {
            ValidationUtil.notNull(request.getControllingPowerRelation(), "controllingPowerRelation");
        }

        ValidationUtil.notNull(request.getPowers(), "powers");
        if (!request.getPowers().isEmpty()) {
            ValidationUtil.notNull(request.getPowersRelation(), "powersRelation");
        }

        if (nonNull(request.getPowerplayState())) {
            ValidationUtil.convertToEnumChecked(request.getPowerplayState(), PowerplayState::valueOf, "powerplayState");
        }

        ValidationUtil.notNull(request.getMinTradeAmount(), "minTradeAmount");
    }
}
