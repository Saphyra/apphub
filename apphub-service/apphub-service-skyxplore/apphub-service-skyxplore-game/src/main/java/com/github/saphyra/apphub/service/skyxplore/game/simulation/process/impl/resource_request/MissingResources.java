package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MissingResources {
    private final int toDeliver;
    private int toRequest;

    public void decreaseToRequest(int delivered) {
        toRequest -= delivered;
    }
}
