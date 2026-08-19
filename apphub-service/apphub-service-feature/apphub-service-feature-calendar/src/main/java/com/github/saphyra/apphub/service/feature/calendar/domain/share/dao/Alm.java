package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Alm {
    private final UUID principal;
    private final PrincipalType principalType;
    private final UUID objectId;
    private final SharedObjectType objectType;
    private final UUID owner; //Owner of the object shared
    private final UUID parent; //Parent of the object shared
    private Set<Grant> grants;
}
