package com.github.saphyra.apphub.api.skyxplore.request;

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
public class CreateChatRoomRequest {
    private String roomTitle;
    private List<UUID> members;
}
