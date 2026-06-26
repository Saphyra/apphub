package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvitationFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID INVITED_USER_ID = UUID.randomUUID();

    private final InvitationFactory underTest = new InvitationFactory();

    @Test
    void create() {
        assertThat(underTest.create(USER_ID, ORGANIZATION_ID, INVITED_USER_ID))
            .returns(INVITED_USER_ID, Invitation::getInvitedUserId)
            .returns(ORGANIZATION_ID, Invitation::getOrganizationId)
            .returns(USER_ID, Invitation::getInvitedBy);
    }
}