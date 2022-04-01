package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
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
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();
}
