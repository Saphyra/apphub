package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Builder
@Data
public class OfferDetail {
    private final TradeMode tradeMode;
    private final StarSystem starSystem;
    private final Double distanceFromReferenceSystem;
    private final CommodityLocationData commodityLocationData;
    private final @Nullable Body body;
    private final Tradeable tradingItem;
    private final @Nullable StarSystemData starSystemData;

    public Double getStationDistanceFromStar(){
        return Optional.ofNullable(body)
            .map(Body::getDistanceFromStar)
            .orElse(null);
    }

    public Power getControllingPower() {
        return Optional.ofNullable(starSystemData)
            .map(StarSystemData::getControllingPower)
            .orElse(null);
    }

    public PowerplayState getPowerplayState() {
        return Optional.ofNullable(starSystemData)
            .map(StarSystemData::getPowerplayState)
            .orElse(null);
    }
}
