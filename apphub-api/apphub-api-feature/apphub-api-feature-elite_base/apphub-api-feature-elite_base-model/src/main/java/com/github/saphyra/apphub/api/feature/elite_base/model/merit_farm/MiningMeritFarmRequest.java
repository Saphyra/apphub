package com.github.saphyra.apphub.api.feature.elite_base.model.merit_farm;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MiningMeritFarmRequest {
    private ReserveLevel minimumReserveLevel;
    private Integer minimumPrice;
    private Integer minimumDemand;
    private Duration maxTimeSinceLastUpdated;
    private PowerplayActivityType powerplayActivity;
}
