package com.github.saphyra.apphub.api.skyxplore.model.game;

import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProcessModel extends GameItem {
    private ProcessType processType;
    private ProcessStatus status;
    private UUID location;
    private String locationType;
    private UUID externalReference;
    private StringStringMap data = new StringStringMap();
}
