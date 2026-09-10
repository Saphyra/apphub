package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;

import java.util.Optional;
import java.util.UUID;

public interface SharedObjectService {
    SharedObjectType getType();

    Optional<SharedObject> getSharedObject(UUID principal, UUID objectId, UUID parent);

    boolean exists(UUID parent, UUID objectId);
}
