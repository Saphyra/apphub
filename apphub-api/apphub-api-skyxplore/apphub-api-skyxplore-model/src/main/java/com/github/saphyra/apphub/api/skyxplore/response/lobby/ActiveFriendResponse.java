package com.github.saphyra.apphub.api.skyxplore.response.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ActiveFriendResponse {
    private UUID friendId;
    private String friendName;
}
