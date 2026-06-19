package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Invitation {
    private final UUID invitedUserId;
    private final UUID organizationId;
    private final UUID invitedBy;
}
