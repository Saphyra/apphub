package com.github.saphyra.apphub.integration.structure.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BlacklistResponse {
    private UUID blacklistId;
    private UUID blockedUserId;
    private String username;
    private String email;
}
