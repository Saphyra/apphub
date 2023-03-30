package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BuildingAllocationModel extends GameItem {
    private UUID buildingId;
    private UUID processId;
}
