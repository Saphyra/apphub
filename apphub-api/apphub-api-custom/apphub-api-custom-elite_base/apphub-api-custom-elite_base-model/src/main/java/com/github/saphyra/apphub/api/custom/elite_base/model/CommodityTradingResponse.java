package com.github.saphyra.apphub.api.custom.elite_base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommodityTradingResponse {
    private UUID starId;
    private String starName;
    private Double starSystemDistance;
    private UUID externalReference;
    private String locationName;
    private String locationType;
    private Double stationDistance;
    private LandingPad landingPad;
    private Integer tradeAmount;
    private Integer price;
    private String controllingPower;
    private List<String> powers;
    private String powerplayState;
    private LocalDateTime lastUpdated;
}
