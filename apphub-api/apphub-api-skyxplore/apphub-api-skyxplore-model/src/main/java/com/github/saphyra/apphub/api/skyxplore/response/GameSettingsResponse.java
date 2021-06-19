package com.github.saphyra.apphub.api.skyxplore.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameSettingsResponse {
    private String universeSize;
    private String systemAmount;
    private String systemSize;
    private String planetSize;
    private String aiPresence;
}
