package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_AUTO_DONE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMINDED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_OCCURRENCE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class OccurrenceMapper extends ConverterBase<Map<String, AttributeValue>, OccurrenceEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(OccurrenceEntity occurrence) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + occurrence.getEventId()).build());
        result.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + occurrence.getOccurrenceId()).build());
        result.put(COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + occurrence.getUserId()).build());
        result.put(COLUMN_DATE_BUCKET, AttributeValue.builder().s(occurrence.getDateBucket()).build());
        result.put(COLUMN_DATE, AttributeValue.builder().s(occurrence.getDate()).build());
        result.put(COLUMN_TIME, AttributeValue.builder().s(occurrence.getTime()).build());
        result.put(COLUMN_STATUS, AttributeValue.builder().s(occurrence.getStatus()).build());
        result.put(COLUMN_NOTE, AttributeValue.builder().s(occurrence.getNote()).build());
        result.put(COLUMN_REMIND_ME_BEFORE_DAYS, AttributeValue.builder().s(occurrence.getRemindMeBeforeDays()).build());
        result.put(COLUMN_REMINDED, AttributeValue.builder().s(occurrence.getReminded()).build());
        result.put(COLUMN_AUTO_DONE, AttributeValue.builder().s(occurrence.getAutoDone()).build());

        return result;
    }

    @Override
    protected OccurrenceEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return OccurrenceEntity.builder()
            .eventId(entity.get(COLUMN_PK).s().substring(PREFIX_EVENT.length()))
            .occurrenceId(entity.get(COLUMN_SK).s().substring(PREFIX_OCCURRENCE.length()))
            .userId(entity.get(COLUMN_USER_ID).s().substring(PREFIX_USER.length()))
            .date(entity.get(COLUMN_DATE).s())
            .dateBucket(entity.get(COLUMN_DATE_BUCKET).s())
            .time(entity.get(COLUMN_TIME).s())
            .status(entity.get(COLUMN_STATUS).s())
            .note(entity.get(COLUMN_NOTE).s())
            .remindMeBeforeDays(entity.get(COLUMN_REMIND_ME_BEFORE_DAYS).s())
            .reminded(entity.get(COLUMN_REMINDED).s())
            .autoDone(entity.get(COLUMN_AUTO_DONE).s())
            .build();
    }
}
