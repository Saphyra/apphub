package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Deconstruction {
    @NonNull
    private final UUID deconstructionId;

    @NonNull
    private final UUID externalReference;

    @NonNull
    private final UUID location;

    private int priority;
}
