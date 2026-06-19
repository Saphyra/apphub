package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class AlmFactory {
    public Alm createAlm(UUID principal, PrincipalType principalType, UUID objectId, ObjectType objectType, List<Operation> operations) {
        return Alm.builder()
            .principal(principal)
            .principalType(principalType)
            .objectId(objectId)
            .objectType(objectType)
            .operations(operations)
            .build();
    }
}
