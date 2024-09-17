package com.github.saphyra.apphub.api.user.model.ban;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BannedDetailsResponse {
    private Boolean permanent;
    private Long bannedUntil;
}
