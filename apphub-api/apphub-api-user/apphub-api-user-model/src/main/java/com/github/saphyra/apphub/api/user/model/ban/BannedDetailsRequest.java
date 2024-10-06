package com.github.saphyra.apphub.api.user.model.ban;

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
public class BannedDetailsRequest {
    private UUID userId;
    private List<String> requiredRoles;
}
