package com.github.saphyra.apphub.api.skyxplore.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SavedGameResponse {
    private UUID gameId;
    private String gameName;
    private String players;
    private Long lastPlayed;
}
