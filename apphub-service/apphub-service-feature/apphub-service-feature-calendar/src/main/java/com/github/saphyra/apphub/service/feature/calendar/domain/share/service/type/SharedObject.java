package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import java.util.UUID;

public record SharedObject(UUID objectId, UUID owner, String name) {
}
