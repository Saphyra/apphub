package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Group {
    private final UUID groupId;
    private UUID ownerId;
    private String name;
    private GroupInvitationType invitationType;
}
