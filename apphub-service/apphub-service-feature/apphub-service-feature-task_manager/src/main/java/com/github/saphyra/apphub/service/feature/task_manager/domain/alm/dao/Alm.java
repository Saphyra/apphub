package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Alm {
    private final UUID principal;
    private final PrincipalType principalType;
    private final UUID objectId;
    private final ObjectType objectType;
    private List<Operation> operations;

    public void addOperation(Operation operation) {
        operations = Stream.concat(
                Stream.of(operation),
                operations.stream()
            )
            .distinct()
            .toList();
    }
}
