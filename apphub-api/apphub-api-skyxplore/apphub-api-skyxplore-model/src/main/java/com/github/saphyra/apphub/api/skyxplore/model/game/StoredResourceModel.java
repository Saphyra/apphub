package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StoredResourceModel extends GameItem {
    private UUID location;
    private UUID containerId;
    private ContainerType containerType;
    private String dataId;
    private UUID allocatedBy;
    private Integer amount;
}
