package com.github.saphyra.apphub.api.feature.calendar.model.request;

import com.github.saphyra.apphub.api.feature.calendar.model.Operation;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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
    private List<Operation> operations;
}
