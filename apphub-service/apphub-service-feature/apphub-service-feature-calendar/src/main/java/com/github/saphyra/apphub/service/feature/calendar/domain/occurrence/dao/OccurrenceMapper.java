package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.lib.dynamodb.AttributeValueUtils.createString;
import static com.github.saphyra.apphub.lib.dynamodb.AttributeValueUtils.getString;
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
        result.put(COLUMN_PK, createString(PREFIX_EVENT + occurrence.getEventId()));
        result.put(COLUMN_SK, createString(PREFIX_OCCURRENCE + occurrence.getOccurrenceId()));
        result.put(COLUMN_USER_ID, createString(PREFIX_USER + occurrence.getUserId()));
        result.put(COLUMN_DATE_BUCKET, createString(occurrence.getDateBucket()));
        result.put(COLUMN_DATE, createString(occurrence.getDate()));
        result.put(COLUMN_TIME, createString(occurrence.getTime()));
        result.put(COLUMN_STATUS, createString(occurrence.getStatus()));
        result.put(COLUMN_NOTE, createString(occurrence.getNote()));
        result.put(COLUMN_REMIND_ME_BEFORE_DAYS, createString(occurrence.getRemindMeBeforeDays()));
        result.put(COLUMN_REMINDED, createString(occurrence.getReminded()));
        result.put(COLUMN_AUTO_DONE, createString(occurrence.getAutoDone()));

        return result;
    }

    @Override
    protected OccurrenceEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return OccurrenceEntity.builder()
            .eventId(getString(entity, COLUMN_PK).substring(PREFIX_EVENT.length()))
            .occurrenceId(getString(entity, COLUMN_SK).substring(PREFIX_OCCURRENCE.length()))
            .userId(getString(entity, COLUMN_USER_ID).substring(PREFIX_USER.length()))
            .date(getString(entity, COLUMN_DATE))
            .dateBucket(getString(entity, COLUMN_DATE_BUCKET))
            .time(getString(entity, COLUMN_TIME))
            .status(getString(entity, COLUMN_STATUS))
            .note(getString(entity, COLUMN_NOTE))
            .remindMeBeforeDays(getString(entity, COLUMN_REMIND_ME_BEFORE_DAYS))
            .reminded(getString(entity, COLUMN_REMINDED))
            .autoDone(getString(entity, COLUMN_AUTO_DONE))
            .build();
    }
}
