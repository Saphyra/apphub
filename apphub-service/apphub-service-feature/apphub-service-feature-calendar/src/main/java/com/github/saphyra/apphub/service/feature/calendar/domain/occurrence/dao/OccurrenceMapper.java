package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EVENT_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_OCCURRENCE_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMINDED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_OCCURRENCE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class OccurrenceMapper extends ConverterBase<Map<String, AttributeValue>, BiWrapper<String, OccurrenceEntity>> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(BiWrapper<String, OccurrenceEntity> domain) {
        OccurrenceEntity occurrence = domain.getEntity2();

        Map<String, AttributeValue> result = new HashMap<>();
        result.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getEntity1()).build());
        result.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + occurrence.getEventId() + "|" + PREFIX_OCCURRENCE + occurrence.getOccurrenceId()).build());
        result.put(COLUMN_EVENT_ID, AttributeValue.builder().s(occurrence.getEventId()).build());
        result.put(COLUMN_OCCURRENCE_ID, AttributeValue.builder().s(occurrence.getOccurrenceId()).build());
        result.put(COLUMN_DATE_BUCKET, AttributeValue.builder().s(occurrence.getDateBucket()).build());
        result.put(COLUMN_DATE, AttributeValue.builder().s(occurrence.getDate()).build());
        result.put(COLUMN_TIME, AttributeValue.builder().s(occurrence.getTime()).build());
        result.put(COLUMN_STATUS, AttributeValue.builder().s(occurrence.getStatus()).build());
        result.put(COLUMN_NOTE, AttributeValue.builder().s(occurrence.getNote()).build());
        result.put(COLUMN_REMIND_ME_BEFORE_DAYS, AttributeValue.builder().s(occurrence.getRemindMeBeforeDays()).build());
        result.put(COLUMN_REMINDED, AttributeValue.builder().s(occurrence.getReminded()).build());

        return result;
    }

    @Override
    protected BiWrapper<String, OccurrenceEntity> processEntityConversion(Map<String, AttributeValue> entity) {
        String userId = entity.get(COLUMN_PK).s().substring(PREFIX_USER.length());
        String sk = entity.get(COLUMN_SK).s();
        int eventIdEndIndex = sk.indexOf("|");

        return new BiWrapper<>(
            userId,
            OccurrenceEntity.builder()
                .eventId(sk.substring(PREFIX_EVENT.length(), eventIdEndIndex))
                .occurrenceId(sk.substring(sk.indexOf(PREFIX_OCCURRENCE) + PREFIX_OCCURRENCE.length()))
                .dateBucket(entity.get(COLUMN_DATE_BUCKET).s())
                .date(entity.get(COLUMN_DATE).s())
                .time(entity.get(COLUMN_TIME).s())
                .status(entity.get(COLUMN_STATUS).s())
                .note(entity.get(COLUMN_NOTE).s())
                .remindMeBeforeDays(entity.get(COLUMN_REMIND_ME_BEFORE_DAYS).s())
                .reminded(entity.get(COLUMN_REMINDED).s())
                .build()
        );
    }
}
