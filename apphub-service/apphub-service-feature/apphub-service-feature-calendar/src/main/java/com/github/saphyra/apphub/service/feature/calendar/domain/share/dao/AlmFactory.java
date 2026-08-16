package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmFactory {
    public Alm create(UUID principal, PrincipalType principalType, UUID objectId, SharedObjectType objectType, UUID owner, UUID parent, List<Grant> grants) {
        return Alm.builder()
            .principal(principal)
            .principalType(principalType)
            .objectId(objectId)
            .objectType(objectType)
            .owner(owner)
            .parent(parent)
            .grants(grants)
            .build();
    }
}
