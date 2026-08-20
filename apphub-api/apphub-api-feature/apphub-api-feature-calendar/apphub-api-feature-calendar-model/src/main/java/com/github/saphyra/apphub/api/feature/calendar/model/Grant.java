package com.github.saphyra.apphub.api.feature.calendar.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public enum Grant {
    SEE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with masked data
    VIEW_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT), //Can query children with real data
    EDIT_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    DELETE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),
    SHARE_CHILDREN(SharedObjectType.LABEL, SharedObjectType.EVENT),

    SEE(SEE_CHILDREN, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Query the main object with masked data
    VIEW(VIEW_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Query the main object
    EDIT(EDIT_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE), //Edit the main object
    DELETE(DELETE_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),
    SHARE(SHARE_CHILDREN, SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),

    EDIT_OPERATIONS(SharedObjectType.LABEL, SharedObjectType.EVENT, SharedObjectType.OCCURRENCE),
    ;

    @Nullable
    private final Grant parent;
    @Getter
    private final List<SharedObjectType> objectTypes;

    Grant(SharedObjectType... sharedObjectType) {
        this(null, Arrays.asList(sharedObjectType));
    }

    Grant(Grant parent, SharedObjectType... sharedObjectType) {
        this(parent, Arrays.asList(sharedObjectType));
    }

    public static Set<Grant> forType(SharedObjectType sharedObjectType) {
        return Arrays.stream(values())
            .filter(grant -> grant.getObjectTypes().contains(sharedObjectType))
            .collect(Collectors.toSet());
    }

    /**
     * @return which roles the children should inherit from this parent role
     */
    public Stream<Grant> projectForChildren() {
        return Arrays.stream(values())
            .filter(grant -> grant.parent == this)
            .flatMap(grant -> Stream.of(this, grant));
    }
}
