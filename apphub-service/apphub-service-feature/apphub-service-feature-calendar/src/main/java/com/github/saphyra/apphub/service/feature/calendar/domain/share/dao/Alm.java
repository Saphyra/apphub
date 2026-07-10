package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Operation;
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
    private final com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType objectType;
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
