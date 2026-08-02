package com.github.saphyra.apphub.api.feature.calendar.model.response;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
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
public class SharedWithResponse {
    private UUID userId;
    private String username;
    private String email;
    private List<Grant> grants;
}
