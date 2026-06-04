package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.*;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class DeprecatedEventConverter extends ConverterBase<DeprecatedEventEntity, DeprecatedEvent> {
    static final String COLUMN_REPETITION_TYPE = "repetition_type";
    static final String COLUMN_REPETITION_DATA = "repetition_data";
    static final String COLUMN_START_DATE = "start_date";
    static final String COLUMN_END_DATE = "end_date";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_REPEAT_FOR_DAYS = "repeat_for_days";
    static final String COLUMN_CONTENT = "content";
    static final String REMIND_ME_BEFORE_DAYS = "remind_me_before_days";
    static final String EXPIRATION_NOTIFIED = "expiration_notified";
    static final String ARCHIVED = "archived";

    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final BooleanEncryptor booleanEncryptor;

    @Override
    protected DeprecatedEventEntity processDomainConversion(DeprecatedEvent domain) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();

        String eventId = uuidConverter.convertDomain(domain.getEventId());
        return DeprecatedEventEntity.builder()
            .eventId(eventId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .repetitionType(stringEncryptor.encrypt(domain.getRepetitionType().name(), userIdFromAccessToken, eventId, COLUMN_REPETITION_TYPE))
            .repetitionData(stringEncryptor.encrypt(domain.getRepetitionData(), userIdFromAccessToken, eventId, COLUMN_REPETITION_DATA))
            .repeatForDays(integerEncryptor.encrypt(domain.getRepeatForDays(), userIdFromAccessToken, eventId, COLUMN_REPEAT_FOR_DAYS))
            .startDate(localDateEncryptor.encrypt(domain.getStartDate(), userIdFromAccessToken, eventId, COLUMN_START_DATE))
            .endDate(localDateEncryptor.encrypt(domain.getEndDate(), userIdFromAccessToken, eventId, COLUMN_END_DATE))
            .time(localTimeEncryptor.encrypt(domain.getTime(), userIdFromAccessToken, eventId, COLUMN_TIME))
            .title(stringEncryptor.encrypt(domain.getTitle(), userIdFromAccessToken, eventId, COLUMN_TITLE))
            .content(stringEncryptor.encrypt(domain.getContent(), userIdFromAccessToken, eventId, COLUMN_CONTENT))
            .remindMeBeforeDays(integerEncryptor.encrypt(domain.getRemindMeBeforeDays(), userIdFromAccessToken, eventId, REMIND_ME_BEFORE_DAYS))
            .expirationNotified(booleanEncryptor.encrypt(domain.isExpirationNotified(), userIdFromAccessToken, eventId, EXPIRATION_NOTIFIED))
            .archived(booleanEncryptor.encrypt(domain.isArchived(), userIdFromAccessToken, eventId, ARCHIVED))
            .build();
    }

    @Override
    protected DeprecatedEvent processEntityConversion(DeprecatedEventEntity entity) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();

        return DeprecatedEvent.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .repetitionType(RepetitionType.valueOf(stringEncryptor.decrypt(entity.getRepetitionType(), userIdFromAccessToken, entity.getEventId(), COLUMN_REPETITION_TYPE)))
            .repetitionData(stringEncryptor.decrypt(entity.getRepetitionData(), userIdFromAccessToken, entity.getEventId(), COLUMN_REPETITION_DATA))
            .repeatForDays(integerEncryptor.decrypt(entity.getRepeatForDays(), userIdFromAccessToken, entity.getEventId(), COLUMN_REPEAT_FOR_DAYS))
            .startDate(localDateEncryptor.decrypt(entity.getStartDate(), userIdFromAccessToken, entity.getEventId(), COLUMN_START_DATE))
            .endDate(localDateEncryptor.decrypt(entity.getEndDate(), userIdFromAccessToken, entity.getEventId(), COLUMN_END_DATE))
            .time(localTimeEncryptor.decrypt(entity.getTime(), userIdFromAccessToken, entity.getEventId(), COLUMN_TIME))
            .title(stringEncryptor.decrypt(entity.getTitle(), userIdFromAccessToken, entity.getEventId(), COLUMN_TITLE))
            .content(Optional.ofNullable(stringEncryptor.decrypt(entity.getContent(), userIdFromAccessToken, entity.getEventId(), COLUMN_CONTENT)).orElse(Constants.EMPTY_STRING))
            .remindMeBeforeDays(integerEncryptor.decrypt(entity.getRemindMeBeforeDays(), userIdFromAccessToken, entity.getEventId(), REMIND_ME_BEFORE_DAYS))
            .expirationNotified(Optional.ofNullable(booleanEncryptor.decrypt(entity.getExpirationNotified(), userIdFromAccessToken, entity.getEventId(), EXPIRATION_NOTIFIED)).orElse(false))
            .archived(Optional.ofNullable(booleanEncryptor.decrypt(entity.getArchived(), userIdFromAccessToken, entity.getEventId(), ARCHIVED)).orElse(false))
            .build();
    }
}
