package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer;

import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.star_system_data.StarSystemData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class OfferDetail {
    private final Offer offer;
    private final ItemLocationData locationData;
    private final StarSystemData starSystemData;
    private final List<Power> powers;
    private final Body body;

    public StationType getStationType() {
        return locationData.getType();
    }

    public Optional<PowerplayState> getPowerplayState() {
        return Optional.ofNullable(starSystemData)
            .map(StarSystemData::getPowerplayState);
    }

    public Optional<List<Power>> getPowers() {
        return Optional.ofNullable(powers);
    }

    public Optional<Power> getControllingPower() {
        return Optional.ofNullable(starSystemData)
            .map(StarSystemData::getControllingPower);
    }

    public Optional<Double> getStationDistanceFromStar() {
        return Optional.ofNullable(body)
            .map(Body::getDistanceFromStar);
    }

    public UUID getStarSystemId() {
        return offer.getStarSystemId();
    }

    public String getStarName() {
        return offer.getStarName();
    }

    public UUID getExternalReference() {
        return offer.getExternalReference();
    }

    public String getLocationName() {
        return locationData.getName();
    }

    public ItemLocationType getLocationType() {
        return offer.getLocationType();
    }

    public Integer getAmount() {
        return offer.getAmount();
    }

    public Integer getPrice() {
        return offer.getPrice();
    }

    public LocalDateTime getLastUpdated() {
        return offer.getLastUpdate();
    }

    public double getDistanceFromReferenceSystem() {
        return offer.getDistanceFromReferenceSystem();
    }
}
