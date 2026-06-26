package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InvitationDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private InvitationRepository repository;

    @InjectMocks
    private InvitationDao underTest;

    @Mock
    private Invitation invitation;

    @Test
    void save() {
        underTest.save(List.of(invitation));

        then(repository).should().saveAll(List.of(invitation));
    }

    @Test
    void getByInvitedUserId() {
        given(repository.getByUserId(USER_ID)).willReturn(List.of(invitation));

        assertThat(underTest.getByInvitedUserId(USER_ID)).containsExactly(invitation);
    }

    @Test
    void getByInvitedUserIdAndOrganizationId() {
        given(repository.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).willReturn(List.of(invitation));

        assertThat(underTest.getByInvitedUserIdAndOrganizationId(USER_ID, ORGANIZATION_ID)).containsExactly(invitation);
    }

    @Test
    void delete() {
        underTest.delete(List.of(invitation));

        then(repository).should().delete(List.of(invitation));
    }
}