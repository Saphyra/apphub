package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class EventConverter extends ConverterBase<EventEntity, Event> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String eventId = uuidConverter.convertDomain(domain.getEventId());

        return EventEntity.builder()
            .eventId(eventId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .repetitionType(domain.getRepetitionType().name())
            .repetitionData(domain.getRepetitionData())
            .repeatForDays(domain.getRepeatForDays().toString())
            .startDate(dateTimeConverter.convertDomain(domain.getStartDate()))
            .endDate(dateTimeConverter.convertDomain(domain.getEndDate()))
            .time(dateTimeConverter.convertDomain(domain.getTime()))
            .title(domain.getTitle())
            .content(domain.getContent())
            .remindMeBeforeDays(String.valueOf(domain.getRemindMeBeforeDays()))
            .expirationNotified(String.valueOf(domain.isExpirationNotified()))
            .archived(String.valueOf(domain.isArchived()))
            .autoDone(String.valueOf(domain.isAutoDone()))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .repetitionType(RepetitionType.valueOf(entity.getRepetitionType()))
            .repetitionData(entity.getRepetitionData())
            .repeatForDays(Integer.parseInt(entity.getRepeatForDays()))
            .startDate(dateTimeConverter.convertToLocalDate(entity.getStartDate()))
            .endDate(dateTimeConverter.convertToLocalDate(entity.getEndDate()))
            .time(dateTimeConverter.convertToLocalTime(entity.getTime()))
            .title(entity.getTitle())
            .content(entity.getContent())
            .remindMeBeforeDays(Integer.parseInt(entity.getRemindMeBeforeDays()))
            .expirationNotified(Boolean.parseBoolean(entity.getExpirationNotified()))
            .archived(Boolean.parseBoolean(entity.getArchived()))
            .autoDone(Boolean.parseBoolean(entity.getAutoDone()))
            .build();
    }
}
