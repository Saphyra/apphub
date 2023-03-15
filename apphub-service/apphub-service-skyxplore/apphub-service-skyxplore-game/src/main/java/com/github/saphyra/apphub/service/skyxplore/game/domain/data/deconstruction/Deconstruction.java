package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Deconstruction {
    private final UUID deconstructionId;
    private final UUID externalReference;
    private int currentWorkPoints;
    private int priority;
}
