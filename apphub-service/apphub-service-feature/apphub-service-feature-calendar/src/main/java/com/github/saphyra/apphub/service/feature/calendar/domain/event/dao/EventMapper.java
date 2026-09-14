package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.lib.dynamodb.AttributeValueUtils.createString;
import static com.github.saphyra.apphub.lib.dynamodb.AttributeValueUtils.getString;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_AUTO_DONE;
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
class EventMapper extends ConverterBase<Map<String, AttributeValue>, EventEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(EventEntity domain) {
        Map<String, AttributeValue> result = new HashMap<>();

        result.put(COLUMN_PK, createString(PREFIX_USER + domain.getUserId()));
        result.put(COLUMN_SK, createString(PREFIX_EVENT + domain.getEventId()));
        result.put(COLUMN_REPETITION_TYPE, createString(domain.getRepetitionType()));
        result.put(COLUMN_REPETITION_DATA, createString(domain.getRepetitionData()));
        result.put(COLUMN_REPEAT_FOR_DAYS, createString(domain.getRepeatForDays()));
        result.put(COLUMN_START_DATE, createString(domain.getStartDate()));
        result.put(COLUMN_TIME, createString(domain.getTime()));
        result.put(COLUMN_TITLE, createString(domain.getTitle()));
        result.put(COLUMN_CONTENT, createString(domain.getContent()));
        result.put(COLUMN_REMIND_ME_BEFORE_DAYS, createString(domain.getRemindMeBeforeDays()));
        result.put(COLUMN_EXPIRATION_NOTIFIED, createString(domain.getExpirationNotified()));
        result.put(COLUMN_ARCHIVED, createString(domain.getArchived()));
        result.put(COLUMN_END_DATE, createString(domain.getEndDate()));
        result.put(COLUMN_AUTO_DONE, createString(domain.getAutoDone()));

        return result;
    }

    @Override
    protected EventEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return EventEntity.builder()
            .userId(getString(entity, COLUMN_PK).substring(PREFIX_USER.length()))
            .eventId(getString(entity, COLUMN_SK).substring(PREFIX_EVENT.length()))
            .repetitionType(getString(entity, COLUMN_REPETITION_TYPE))
            .repetitionData(getString(entity, COLUMN_REPETITION_DATA))
            .repeatForDays(getString(entity, COLUMN_REPEAT_FOR_DAYS))
            .startDate(getString(entity, COLUMN_START_DATE))
            .time(getString(entity, COLUMN_TIME))
            .endDate(getString(entity, COLUMN_END_DATE))
            .title(getString(entity, COLUMN_TITLE))
            .content(getString(entity, COLUMN_CONTENT))
            .remindMeBeforeDays(getString(entity, COLUMN_REMIND_ME_BEFORE_DAYS))
            .expirationNotified(getString(entity, COLUMN_EXPIRATION_NOTIFIED))
            .archived(getString(entity, COLUMN_ARCHIVED))
            .autoDone(getString(entity, COLUMN_AUTO_DONE))
            .build();
    }
}
