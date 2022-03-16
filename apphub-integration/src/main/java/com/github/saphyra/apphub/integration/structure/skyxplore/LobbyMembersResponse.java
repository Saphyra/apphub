package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LobbyMembersResponse {
    private LobbyMemberResponse host;
    private List<LobbyMemberResponse> members;
    private List<String> alliances;
}