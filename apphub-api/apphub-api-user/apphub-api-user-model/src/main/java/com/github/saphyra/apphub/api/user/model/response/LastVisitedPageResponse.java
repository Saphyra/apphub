package com.github.saphyra.apphub.api.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LastVisitedPageResponse {
    private String pageUrl;
    private LocalDateTime lastAccess;
}
