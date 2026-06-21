package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InvitationMapperTest {
	private static final UUID INVITED_USER_ID = UUID.randomUUID();
	private static final UUID ORGANIZATION_ID = UUID.randomUUID();
	private static final UUID INVITED_BY = UUID.randomUUID();
	private static final String INVITED_USER_ID_STRING = "invited-user-id";
	private static final String ORGANIZATION_ID_STRING = "organization-id";
	private static final String INVITED_BY_STRING = "invited-by";

	@Mock
	private UuidConverter uuidConverter;

	@InjectMocks
	private InvitationMapper underTest;

	@Test
	void convertDomain() {
		Invitation domain = Invitation.builder()
			.invitedUserId(INVITED_USER_ID)
			.organizationId(ORGANIZATION_ID)
			.invitedBy(INVITED_BY)
			.build();

		given(uuidConverter.convertDomain(INVITED_USER_ID)).willReturn(INVITED_USER_ID_STRING);
		given(uuidConverter.convertDomain(ORGANIZATION_ID)).willReturn(ORGANIZATION_ID_STRING);
		given(uuidConverter.convertDomain(INVITED_BY)).willReturn(INVITED_BY_STRING);

		Map<String, AttributeValue> result = underTest.convertDomain(domain);

		assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + INVITED_USER_ID_STRING);
		assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_ORGANIZATION + ORGANIZATION_ID_STRING + "|" + PREFIX_USER + INVITED_BY_STRING);
	}

	@Test
	void convertEntity() {
		Map<String, AttributeValue> entity = Map.of(
			COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + INVITED_USER_ID_STRING).build(),
			COLUMN_SK, AttributeValue.builder().s(PREFIX_ORGANIZATION + ORGANIZATION_ID_STRING + "|" + PREFIX_USER + INVITED_BY_STRING).build()
		);

		given(uuidConverter.convertEntity(INVITED_USER_ID_STRING)).willReturn(INVITED_USER_ID);
		given(uuidConverter.convertEntity(ORGANIZATION_ID_STRING)).willReturn(ORGANIZATION_ID);
		given(uuidConverter.convertEntity(INVITED_BY_STRING)).willReturn(INVITED_BY);

		assertThat(underTest.convertEntity(entity))
			.returns(INVITED_USER_ID, Invitation::getInvitedUserId)
			.returns(ORGANIZATION_ID, Invitation::getOrganizationId)
			.returns(INVITED_BY, Invitation::getInvitedBy);
	}
}