package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_CREATED_AT;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_EXPIRATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_LAST_MODIFIED;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_NOTIFICATION_TYPE;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_NOTIFICATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {
	private static final String RECIPIENT = "user-123";
	private static final String NOTIFICATION_ID = "notification-456";
	private static final String STATUS = "UNREAD";
	private static final String NOTIFICATION_TYPE = "TASK_ASSIGNED";
	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 6, 21, 10, 30, 0);
	private static final LocalDateTime LAST_MODIFIED = LocalDateTime.of(2026, 6, 21, 15, 45, 30);
	private static final LocalDateTime EXPIRATION = LocalDateTime.of(2026, 7, 21, 10, 30, 0);
	private static final String DATA = "{\"taskId\": \"task-123\", \"title\": \"Task Title\"}";
	private static final long CREATED_AT_EPOCH = 1750602600L;
	private static final long LAST_MODIFIED_EPOCH = 1750620330L;
	private static final long EXPIRATION_EPOCH = 1753294200L;

	@Mock
	private DateTimeUtil dateTimeUtil;

	@InjectMocks
	private NotificationMapper underTest;

	@Test
	void convertDomain() {
		NotificationEntity domain = NotificationEntity.builder()
			.recipient(RECIPIENT)
			.notificationId(NOTIFICATION_ID)
			.status(STATUS)
			.notificationType(NOTIFICATION_TYPE)
			.createdAt(CREATED_AT)
			.lastModified(LAST_MODIFIED)
			.expiration(EXPIRATION)
			.data(DATA)
			.build();

		given(dateTimeUtil.toEpochSecond(CREATED_AT)).willReturn(CREATED_AT_EPOCH);
		given(dateTimeUtil.toEpochSecond(LAST_MODIFIED)).willReturn(LAST_MODIFIED_EPOCH);
		given(dateTimeUtil.toEpochSecond(EXPIRATION)).willReturn(EXPIRATION_EPOCH);

		Map<String, AttributeValue> result = underTest.convertDomain(domain);

		assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + RECIPIENT);
		assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_NOTIFICATION + NOTIFICATION_ID);
		assertThat(result.get(COLUMN_STATUS).s()).isEqualTo(STATUS);
		assertThat(result.get(COLUMN_NOTIFICATION_TYPE).s()).isEqualTo(NOTIFICATION_TYPE);
		assertThat(result.get(COLUMN_CREATED_AT).n()).isEqualTo(String.valueOf(CREATED_AT_EPOCH));
		assertThat(result.get(COLUMN_LAST_MODIFIED).n()).isEqualTo(String.valueOf(LAST_MODIFIED_EPOCH));
		assertThat(result.get(COLUMN_EXPIRATION).n()).isEqualTo(String.valueOf(EXPIRATION_EPOCH));
		assertThat(result.get(COLUMN_DATA).s()).isEqualTo(DATA);
	}

	@Test
	void convertDomain_withoutExpiration() {
		NotificationEntity domain = NotificationEntity.builder()
			.recipient(RECIPIENT)
			.notificationId(NOTIFICATION_ID)
			.status(STATUS)
			.notificationType(NOTIFICATION_TYPE)
			.createdAt(CREATED_AT)
			.lastModified(LAST_MODIFIED)
			.expiration(null)
			.data(DATA)
			.build();

		given(dateTimeUtil.toEpochSecond(CREATED_AT)).willReturn(CREATED_AT_EPOCH);
		given(dateTimeUtil.toEpochSecond(LAST_MODIFIED)).willReturn(LAST_MODIFIED_EPOCH);

		Map<String, AttributeValue> result = underTest.convertDomain(domain);

		assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + RECIPIENT);
		assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_NOTIFICATION + NOTIFICATION_ID);
		assertThat(result.get(COLUMN_STATUS).s()).isEqualTo(STATUS);
		assertThat(result.get(COLUMN_NOTIFICATION_TYPE).s()).isEqualTo(NOTIFICATION_TYPE);
		assertThat(result.get(COLUMN_CREATED_AT).n()).isEqualTo(String.valueOf(CREATED_AT_EPOCH));
		assertThat(result.get(COLUMN_LAST_MODIFIED).n()).isEqualTo(String.valueOf(LAST_MODIFIED_EPOCH));
		assertThat(result.get(COLUMN_EXPIRATION)).isNull();
		assertThat(result.get(COLUMN_DATA).s()).isEqualTo(DATA);
	}

	@Test
	void convertEntity() {
		Map<String, AttributeValue> entity = Map.of(
			COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + RECIPIENT).build(),
			COLUMN_SK, AttributeValue.builder().s(PREFIX_NOTIFICATION + NOTIFICATION_ID).build(),
			COLUMN_STATUS, AttributeValue.builder().s(STATUS).build(),
			COLUMN_NOTIFICATION_TYPE, AttributeValue.builder().s(NOTIFICATION_TYPE).build(),
			COLUMN_CREATED_AT, AttributeValue.builder().n(String.valueOf(CREATED_AT_EPOCH)).build(),
			COLUMN_LAST_MODIFIED, AttributeValue.builder().n(String.valueOf(LAST_MODIFIED_EPOCH)).build(),
			COLUMN_EXPIRATION, AttributeValue.builder().n(String.valueOf(EXPIRATION_EPOCH)).build(),
			COLUMN_DATA, AttributeValue.builder().s(DATA).build()
		);

		given(dateTimeUtil.fromEpochSecond(CREATED_AT_EPOCH)).willReturn(CREATED_AT);
		given(dateTimeUtil.fromEpochSecond(LAST_MODIFIED_EPOCH)).willReturn(LAST_MODIFIED);
		given(dateTimeUtil.fromEpochSecond(EXPIRATION_EPOCH)).willReturn(EXPIRATION);

		assertThat(underTest.convertEntity(entity))
			.returns(RECIPIENT, NotificationEntity::getRecipient)
			.returns(NOTIFICATION_ID, NotificationEntity::getNotificationId)
			.returns(STATUS, NotificationEntity::getStatus)
			.returns(NOTIFICATION_TYPE, NotificationEntity::getNotificationType)
			.returns(CREATED_AT, NotificationEntity::getCreatedAt)
			.returns(LAST_MODIFIED, NotificationEntity::getLastModified)
			.returns(EXPIRATION, NotificationEntity::getExpiration)
			.returns(DATA, NotificationEntity::getData);
	}

	@Test
	void convertEntity_withoutExpiration() {
		Map<String, AttributeValue> entity = Map.of(
			COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + RECIPIENT).build(),
			COLUMN_SK, AttributeValue.builder().s(PREFIX_NOTIFICATION + NOTIFICATION_ID).build(),
			COLUMN_STATUS, AttributeValue.builder().s(STATUS).build(),
			COLUMN_NOTIFICATION_TYPE, AttributeValue.builder().s(NOTIFICATION_TYPE).build(),
			COLUMN_CREATED_AT, AttributeValue.builder().n(String.valueOf(CREATED_AT_EPOCH)).build(),
			COLUMN_LAST_MODIFIED, AttributeValue.builder().n(String.valueOf(LAST_MODIFIED_EPOCH)).build(),
			COLUMN_DATA, AttributeValue.builder().s(DATA).build()
		);

		given(dateTimeUtil.fromEpochSecond(CREATED_AT_EPOCH)).willReturn(CREATED_AT);
		given(dateTimeUtil.fromEpochSecond(LAST_MODIFIED_EPOCH)).willReturn(LAST_MODIFIED);

		assertThat(underTest.convertEntity(entity))
			.returns(RECIPIENT, NotificationEntity::getRecipient)
			.returns(NOTIFICATION_ID, NotificationEntity::getNotificationId)
			.returns(STATUS, NotificationEntity::getStatus)
			.returns(NOTIFICATION_TYPE, NotificationEntity::getNotificationType)
			.returns(CREATED_AT, NotificationEntity::getCreatedAt)
			.returns(LAST_MODIFIED, NotificationEntity::getLastModified)
			.returns(null, NotificationEntity::getExpiration)
			.returns(DATA, NotificationEntity::getData);
	}
}