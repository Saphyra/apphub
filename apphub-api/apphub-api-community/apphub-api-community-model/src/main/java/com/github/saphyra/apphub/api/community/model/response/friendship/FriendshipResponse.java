package com.github.saphyra.apphub.api.community.model.response.friendship;

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
    private String username;
    private String email;
}
