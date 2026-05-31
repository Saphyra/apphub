package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_END_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EXPIRATION_NOTIFIED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPEAT_FOR_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_DATA;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REPETITION_TYPE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_START_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TITLE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
//TODO unit test
class EventMapper extends ConverterBase<Map<String, AttributeValue>, EventEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(EventEntity domain) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build());
        result.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + domain.getEventId()).build());
        result.put(COLUMN_REPETITION_TYPE, AttributeValue.builder().s(domain.getRepetitionType()).build());
        result.put(COLUMN_REPETITION_DATA, AttributeValue.builder().s(domain.getRepetitionData()).build());
        result.put(COLUMN_REPEAT_FOR_DAYS, AttributeValue.builder().s(domain.getRepeatForDays()).build());
        result.put(COLUMN_START_DATE, AttributeValue.builder().s(domain.getStartDate()).build());
        result.put(COLUMN_TIME, AttributeValue.builder().s(domain.getTime()).build());
        result.put(COLUMN_TITLE, AttributeValue.builder().s(domain.getTitle()).build());
        result.put(COLUMN_CONTENT, AttributeValue.builder().s(domain.getContent()).build());
        result.put(COLUMN_REMIND_ME_BEFORE_DAYS, AttributeValue.builder().s(domain.getRemindMeBeforeDays()).build());
        result.put(COLUMN_EXPIRATION_NOTIFIED, AttributeValue.builder().s(domain.getExpirationNotified()).build());
        result.put(COLUMN_ARCHIVED, AttributeValue.builder().s(domain.getArchived()).build());
        result.put(COLUMN_END_DATE, AttributeValue.builder().s(domain.getEndDate()).build());

        return result;
    }

    @Override
    protected EventEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return EventEntity.builder()
            .userId(entity.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .eventId(entity.get(COLUMN_SK).s().substring(PREFIX_EVENT.length()))
            .repetitionType(entity.get(COLUMN_REPETITION_TYPE).s())
            .repetitionData(entity.get(COLUMN_REPETITION_DATA).s())
            .repeatForDays(entity.get(COLUMN_REPEAT_FOR_DAYS).s())
            .startDate(entity.get(COLUMN_START_DATE).s())
            .time(entity.get(COLUMN_TIME).s())
            .endDate(Optional.ofNullable(entity.get(COLUMN_END_DATE)).map(AttributeValue::s).orElse(null))
            .title(entity.get(COLUMN_TITLE).s())
            .content(entity.get(COLUMN_CONTENT).s())
            .remindMeBeforeDays(entity.get(COLUMN_REMIND_ME_BEFORE_DAYS).s())
            .expirationNotified(entity.get(COLUMN_EXPIRATION_NOTIFIED).s())
            .archived(entity.get(COLUMN_ARCHIVED).s())
            .build();
    }
}
