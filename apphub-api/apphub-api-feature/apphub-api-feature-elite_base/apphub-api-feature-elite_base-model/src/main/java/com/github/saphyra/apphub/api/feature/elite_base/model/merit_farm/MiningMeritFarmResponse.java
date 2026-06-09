package com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiningMeritFarmResponse {
    private UUID sourceStarSystemId; //Mine here
    private String sourceStarSystemName;
    //TODO add reserve level
    private UUID targetStarSystemId; //Sell in this system...
    private String targetStarSystemName;
    private UUID stationId; //...at this station
    private String stationName;
    private String commodityName;
    private Integer demand;
    private Integer price;
    //TODO add offer last update
    private PowerplayActivityType activityType;
}
