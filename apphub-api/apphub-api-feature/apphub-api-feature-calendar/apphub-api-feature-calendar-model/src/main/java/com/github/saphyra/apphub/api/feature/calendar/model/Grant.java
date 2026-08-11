package com.github.saphyra.apphub.api.feature.calendar.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public enum Grant {
    SEE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with masked data
    VIEW_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with real data
    EDIT_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    DELETE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    SEE(SEE_CHILDREN, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Query the main object with masked data
    VIEW(VIEW_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Query the main object
    EDIT(EDIT_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Edit the main object
    DELETE(DELETE_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),
    SHARE(SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),
    EDIT_OPERATIONS(SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),
    ;

    private final Grant parent;
    @Getter
    private final List<SharedObjectType> objectTypes;

    Grant(SharedObjectType... sharedObjectType) {
        this(null, Arrays.asList(sharedObjectType));
    }

    Grant(Grant parent, SharedObjectType... sharedObjectType) {
        this(parent, Arrays.asList(sharedObjectType));
    }

    public Optional<Grant> toParent() {
        return Optional.ofNullable(parent);
    }
}
