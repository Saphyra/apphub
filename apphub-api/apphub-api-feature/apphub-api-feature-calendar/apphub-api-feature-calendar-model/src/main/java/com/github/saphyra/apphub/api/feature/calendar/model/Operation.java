package com.github.saphyra.apphub.api.feature.calendar.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum Operation {
    SEE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with masked data
    VIEW_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with real data
    EDIT_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    DELETE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    EDIT(SharedObjectType.LABEL, SharedObjectType.EVENT), //Edit the main object
    DELETE(SharedObjectType.LABEL, SharedObjectType.EVENT),
    SHARE(SharedObjectType.LABEL, SharedObjectType.EVENT),
    EDIT_OPERATIONS(SharedObjectType.LABEL, SharedObjectType.EVENT),
    ;

    @Getter
    private final List<SharedObjectType> objectTypes;

    Operation(SharedObjectType... sharedObjectType) {
        this(Arrays.asList(sharedObjectType));
    }
}
