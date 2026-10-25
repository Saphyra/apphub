package com.github.saphyra.apphub.service.feature.elite_base.processor.dao;

import java.util.UUID;

public interface ItemLocationData {
    StationType getType();

    UUID getId();

    String getName();

    UUID getBodyId();
}
