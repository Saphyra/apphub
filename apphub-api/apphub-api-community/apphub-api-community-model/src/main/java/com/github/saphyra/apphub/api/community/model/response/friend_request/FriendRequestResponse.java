package com.github.saphyra.apphub.api.community.model.response.friend_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FriendRequestResponse {
    private UUID friendRequestId;
    private String username;
    private String email;
}
