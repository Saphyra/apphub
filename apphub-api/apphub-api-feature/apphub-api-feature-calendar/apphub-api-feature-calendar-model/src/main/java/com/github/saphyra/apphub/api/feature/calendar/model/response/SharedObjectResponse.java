package com.github.saphyra.apphub.api.feature.calendar.model.response;

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
public class SharedObjectResponse {
    private UUID objectId;
    private String name;
    private UUID owner;
    private List<SharedWithResponse> sharedWith;
}
