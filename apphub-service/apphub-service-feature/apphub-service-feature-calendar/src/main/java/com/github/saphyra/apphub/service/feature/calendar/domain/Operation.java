package com.github.saphyra.apphub.service.feature.calendar.domain;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum Operation {
    DELETE(Grant.DELETE),
    EDIT(Grant.EDIT, Grant.VIEW),
    ;

    @Getter
    private final List<Grant> requiredGrants;

    Operation(Grant... requiredGrants) {
        this(Arrays.asList(requiredGrants));
    }
}
