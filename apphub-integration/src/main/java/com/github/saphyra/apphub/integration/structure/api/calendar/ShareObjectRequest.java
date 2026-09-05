package com.github.saphyra.apphub.integration.structure.api.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShareObjectRequest {
    private UUID sharedWith;
    private UUID owner;
    private UUID objectId;
    private UUID parent;
    private SharedObjectType type;
    private Set<Grant> grants;
}
