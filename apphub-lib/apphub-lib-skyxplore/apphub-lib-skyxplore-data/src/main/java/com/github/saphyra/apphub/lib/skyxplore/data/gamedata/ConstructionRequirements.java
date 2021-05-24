package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionRequirements {
    private Integer requiredWorkPoints;
    private Map<String, Integer> requiredResources;
}
