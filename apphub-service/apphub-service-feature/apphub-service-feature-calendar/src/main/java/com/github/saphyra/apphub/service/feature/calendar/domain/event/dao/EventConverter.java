package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalTimeEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_END_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EXPIRATION_NOTIFIED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPEAT_FOR_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_TYPE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TITLE;

@Component
@RequiredArgsConstructor
//TODO unit test
class EventConverter extends ConverterBase<EventEntity, Event> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String eventId = uuidConverter.convertDomain(domain.getEventId());

        return EventEntity.builder()
            .eventId(eventId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .repetitionType(stringEncryptor.encrypt(domain.getRepetitionType().name(), userId, eventId, COLUMN_REPETITION_TYPE))
            .repetitionData(stringEncryptor.encrypt(domain.getRepetitionData(), userId, eventId, COLUMN_REPETITION_DATA))
            .repeatForDays(integerEncryptor.encrypt(domain.getRepeatForDays(), userId, eventId, COLUMN_REPEAT_FOR_DAYS))
            .startDate(localDateEncryptor.encrypt(domain.getStartDate(), userId, eventId, COLUMN_START_DATE))
            .endDate(localDateEncryptor.encrypt(domain.getEndDate(), userId, eventId, COLUMN_END_DATE))
            .time(localTimeEncryptor.encrypt(domain.getTime(), userId, eventId, COLUMN_TIME))
            .title(stringEncryptor.encrypt(domain.getTitle(), userId, eventId, COLUMN_TITLE))
            .content(stringEncryptor.encrypt(domain.getContent(), userId, eventId, COLUMN_CONTENT))
            .remindMeBeforeDays(integerEncryptor.encrypt(domain.getRemindMeBeforeDays(), userId, eventId, COLUMN_REMIND_ME_BEFORE_DAYS))
            .expirationNotified(booleanEncryptor.encrypt(domain.isExpirationNotified(), userId, eventId, COLUMN_EXPIRATION_NOTIFIED))
            .archived(booleanEncryptor.encrypt(domain.isArchived(), userId, eventId, COLUMN_ARCHIVED))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .repetitionType(RepetitionType.valueOf(stringEncryptor.decrypt(entity.getRepetitionType(), userId, entity.getEventId(), COLUMN_REPETITION_TYPE)))
            .repetitionData(stringEncryptor.decrypt(entity.getRepetitionData(), userId, entity.getEventId(), COLUMN_REPETITION_DATA))
            .repeatForDays(integerEncryptor.decrypt(entity.getRepeatForDays(), userId, entity.getEventId(), COLUMN_REPEAT_FOR_DAYS))
            .startDate(localDateEncryptor.decrypt(entity.getStartDate(), userId, entity.getEventId(), COLUMN_START_DATE))
            .endDate(localDateEncryptor.decrypt(entity.getEndDate(), userId, entity.getEventId(), COLUMN_END_DATE))
            .time(localTimeEncryptor.decrypt(entity.getTime(), userId, entity.getEventId(), COLUMN_TIME))
            .title(stringEncryptor.decrypt(entity.getTitle(), userId, entity.getEventId(), COLUMN_TITLE))
            .content(Optional.ofNullable(stringEncryptor.decrypt(entity.getContent(), userId, entity.getEventId(), COLUMN_CONTENT)).orElse(Constants.EMPTY_STRING))
            .remindMeBeforeDays(integerEncryptor.decrypt(entity.getRemindMeBeforeDays(), userId, entity.getEventId(), COLUMN_REMIND_ME_BEFORE_DAYS))
            .expirationNotified(booleanEncryptor.decrypt(entity.getExpirationNotified(), userId, entity.getEventId(), COLUMN_EXPIRATION_NOTIFIED))
            .archived(booleanEncryptor.decrypt(entity.getArchived(), userId, entity.getEventId(), COLUMN_ARCHIVED))
            .build();
    }
}
