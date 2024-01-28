package com.github.saphyra.apphub.api.skyxplore.response.game.citizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CitizenAssignmentResponse {
    private String type;
    private Object data;
}
