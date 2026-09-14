package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public enum Operation {
    DELETE(Grant.DELETE),
    EDIT(Grant.EDIT, Grant.VIEW),
    ;

    @Getter
    private final Set<Grant> requiredGrants;

    Operation(Grant... requiredGrants) {
        this(Set.of(requiredGrants));
    }
}
