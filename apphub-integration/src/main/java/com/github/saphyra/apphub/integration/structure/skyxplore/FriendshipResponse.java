package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FriendshipResponse {
    private UUID friendshipId;
    private UUID friendId;
    private String friendName;
}
