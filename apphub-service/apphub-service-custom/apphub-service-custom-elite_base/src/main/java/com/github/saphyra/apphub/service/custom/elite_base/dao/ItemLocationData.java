package com.github.saphyra.apphub.service.custom.elite_base.dao;

import java.util.UUID;

public interface ItemLocationData {
    StationType getType();

    UUID getId();

    String getName();

    UUID getBodyId();
}
