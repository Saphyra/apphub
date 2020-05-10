package com.github.saphyra.apphub.service.modules.dao.favorite;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class Favorite {
    @NonNull
    private final UUID userId;

    @NonNull
    private final String module;
    private boolean favorite;
}
