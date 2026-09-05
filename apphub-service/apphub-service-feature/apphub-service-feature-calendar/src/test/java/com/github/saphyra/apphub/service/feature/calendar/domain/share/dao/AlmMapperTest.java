package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_OBJECT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_OPERATIONS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PRINCIPAL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AlmMapperTest {
	private static final UUID PRINCIPAL = UUID.randomUUID();
	private static final UUID OBJECT_ID = UUID.randomUUID();
	private static final UUID OWNER = UUID.randomUUID();
	private static final UUID PARENT = UUID.randomUUID();
	private static final String PRINCIPAL_STRING = "principal";
	private static final String OBJECT_ID_STRING = "object-id";
	private static final String OWNER_STRING = "owner";
	private static final String PARENT_STRING = "parent";

	@Mock
	private UuidConverter uuidConverter;

	@InjectMocks
	private AlmMapper underTest;

	@Test
	void convertDomain() {
		Alm domain = Alm.builder()
			.principal(PRINCIPAL)
			.principalType(PrincipalType.USER)
			.objectId(OBJECT_ID)
			.objectType(SharedObjectType.EVENT)
			.owner(OWNER)
			.parent(PARENT)
			.grants(Set.of(Grant.VIEW, Grant.EDIT))
			.build();

		given(uuidConverter.convertDomain(PRINCIPAL)).willReturn(PRINCIPAL_STRING);
		given(uuidConverter.convertDomain(OBJECT_ID)).willReturn(OBJECT_ID_STRING);
		given(uuidConverter.convertDomain(OWNER)).willReturn(OWNER_STRING);
		given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

		Map<String, AttributeValue> result = underTest.convertDomain(domain);

		assertThat(result.get(COLUMN_PRINCIPAL).s()).isEqualTo(PrincipalType.USER + "#" + PRINCIPAL_STRING);
		assertThat(result.get(COLUMN_OBJECT).s()).isEqualTo(SharedObjectType.EVENT + "#" + OBJECT_ID_STRING);
		assertThat(result.get(COLUMN_USER_ID).s()).isEqualTo(PREFIX_USER + OWNER_STRING);
		assertThat(result.get(COLUMN_PARENT).s()).isEqualTo(PARENT_STRING);
		assertThat(result.get(COLUMN_OPERATIONS).ss()).containsExactlyInAnyOrder(Grant.VIEW.name(), Grant.EDIT.name());
	}

	@Test
	void convertEntity() {
		Map<String, AttributeValue> entity = Map.ofEntries(
			Map.entry(COLUMN_PRINCIPAL, AttributeValue.builder().s(PrincipalType.USER + "#" + PRINCIPAL_STRING).build()),
			Map.entry(COLUMN_OBJECT, AttributeValue.builder().s(SharedObjectType.EVENT + "#" + OBJECT_ID_STRING).build()),
			Map.entry(COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + OWNER_STRING).build()),
			Map.entry(COLUMN_PARENT, AttributeValue.builder().s(PARENT_STRING).build()),
			Map.entry(COLUMN_OPERATIONS, AttributeValue.builder().ss(Grant.VIEW.name(), Grant.EDIT.name()).build())
		);

		given(uuidConverter.convertEntity(PRINCIPAL_STRING)).willReturn(PRINCIPAL);
		given(uuidConverter.convertEntity(OBJECT_ID_STRING)).willReturn(OBJECT_ID);
		given(uuidConverter.convertEntity(OWNER_STRING)).willReturn(OWNER);
		given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);

		Alm result = underTest.convertEntity(entity);

		assertThat(result)
			.returns(PrincipalType.USER, Alm::getPrincipalType)
			.returns(PRINCIPAL, Alm::getPrincipal)
			.returns(SharedObjectType.EVENT, Alm::getObjectType)
			.returns(OBJECT_ID, Alm::getObjectId)
			.returns(OWNER, Alm::getOwner)
			.returns(PARENT, Alm::getParent);
		assertThat(result.getGrants()).containsExactlyInAnyOrder(Grant.VIEW, Grant.EDIT);
	}

}