package com.github.saphyra.apphub.service.community.group.dao.member;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupMember {
    private final UUID groupMemberId;
    private final UUID groupId;
    private final UUID userId;
    private boolean canInvite;
    private boolean canKick;
}
