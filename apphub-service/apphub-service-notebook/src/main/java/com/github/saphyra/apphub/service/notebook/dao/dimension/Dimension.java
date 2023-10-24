package com.github.saphyra.apphub.service.notebook.dao.dimension;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
public class Dimension {
    @NonNull
    private final UUID dimensionId;

    @NonNull
    private final UUID userId;

    @NonNull
    private UUID externalReference;

    @NonNull
    private Integer index;
}
