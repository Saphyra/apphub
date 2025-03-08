package com.github.saphyra.apphub.api.custom.elite_base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommodityTradingRequest {
    private String tradeMode;
    private UUID referenceStarId;
    private String commodity;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer maxStarSystemDistance;
    private Integer maxStationDistance;
    private Boolean includeUnknownStationDistance;
    private LandingPad minLandingPad;
    private Boolean includeUnknownLandingPad;
    private Duration maxTimeSinceLastUpdated;
    private Boolean includeSurfaceStations;
    private Boolean includeFleetCarriers;
    private List<String> controllingPowers;
    private Relation controllingPowerRelation;
    private List<String> powers;
    private Relation powersRelation;
    private String powerplayState;
    private Integer minTradeAmount;
}
