package com.github.saphyra.apphub.api.feature.calendar.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum Operation {
    SEE_CHILDREN(SharedObjectType.LABEL), //Can query children with masked data
    VIEW_CHILDREN(SharedObjectType.LABEL), //Can query children with real data
    EDIT_CHILDREN(SharedObjectType.LABEL),
    DELETE_CHILDREN(SharedObjectType.LABEL),
    EDIT(SharedObjectType.LABEL), //Edit the main object
    DELETE(SharedObjectType.LABEL),
    SHARE(SharedObjectType.LABEL),
    EDIT_OPERATIONS(SharedObjectType.LABEL),
    ;

    @Getter
    private final List<SharedObjectType> objectTypes;

    Operation(SharedObjectType... sharedObjectType) {
        this(Arrays.asList(sharedObjectType));
    }
}
