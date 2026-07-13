package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Operation;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class AlmFactory {
    public Alm createAlm(UUID principal, PrincipalType principalType, UUID objectId, SharedObjectType objectType, UUID owner, List<Operation> operations) {
        return Alm.builder()
            .principal(principal)
            .principalType(principalType)
            .objectId(objectId)
            .objectType(objectType)
            .owner(owner)
            .operations(operations)
            .build();
    }
}
