package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation;

import com.github.saphyra.apphub.api.feature.task_manager.model.invitation.InvitationResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.service.InvitationService;
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
class InvitationControllerImplTest {
	private static final UUID USER_ID = UUID.randomUUID();
	private static final UUID ORGANIZATION_ID = UUID.randomUUID();

	@Mock
	private InvitationService invitationService;

	@InjectMocks
	private InvitationControllerImpl underTest;

	@Mock
	private AccessToken accessToken;

	@Mock
	private InvitationResponse invitationResponse;

	@Test
	void getInvitations() {
		given(accessToken.getUserId()).willReturn(USER_ID);
		given(invitationService.getInvitations(USER_ID)).willReturn(List.of(invitationResponse));

		assertThat(underTest.getInvitations(accessToken)).containsExactly(invitationResponse);
	}

	@Test
	void acceptInvitation() {
		given(accessToken.getUserId()).willReturn(USER_ID);

		underTest.acceptInvitation(ORGANIZATION_ID, accessToken);

		then(invitationService).should().acceptInvitation(USER_ID, ORGANIZATION_ID);
	}

	@Test
	void rejectInvitation() {
		given(accessToken.getUserId()).willReturn(USER_ID);

		underTest.rejectInvitation(ORGANIZATION_ID, accessToken);

		then(invitationService).should().rejectInvitation(USER_ID, ORGANIZATION_ID);
	}
}