package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
//TODO unit test
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;
    private final DateTimeConverter dateTimeConverter;
    private final OccurrenceRepository occurrenceRepository;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String occurrenceId = uuidConverter.convertDomain(domain.getOccurrenceId());

        return OccurrenceEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .occurrenceId(occurrenceId)
            .dateBucket(toDateBucket(domain.getDate()))
            .date(dateTimeConverter.convertDomain(domain.getDate()))
            .time(dateTimeConverter.convertDomain(domain.getTime()))
            .status(domain.getStatus().name())
            .note(domain.getNote())
            .remindMeBeforeDays(Optional.ofNullable(domain.getRemindMeBeforeDays()).map(String::valueOf).orElse(null))
            .reminded(String.valueOf(domain.isReminded()))
            .autoDone(Optional.ofNullable(domain.getAutoDone()).map(String::valueOf).orElse(null))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        LocalDate date = dateTimeConverter.convertToLocalDate(entity.getDate());
        return Occurrence.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .date(date)
            .time(dateTimeConverter.convertToLocalTime(entity.getTime()))
            .status(syncStatus(entity, entity.getUserId(), date))
            .note(entity.getNote())
            .remindMeBeforeDays(Optional.ofNullable(entity.getRemindMeBeforeDays()).map(Integer::valueOf).orElse(null))
            .reminded(Boolean.parseBoolean(entity.getReminded()))
            .autoDone(Optional.ofNullable(entity.getAutoDone()).map(Boolean::valueOf).orElse(null))
            .build();
    }

    private OccurrenceStatus syncStatus(OccurrenceEntity entity, String userId, LocalDate date) {
        OccurrenceStatus savedStatus = OccurrenceStatus.valueOf(entity.getStatus());

        if (savedStatus == OccurrenceStatus.PENDING && dateTimeUtil.getCurrentDate().isAfter(date)) {
            String newStatus = OccurrenceStatus.EXPIRED.name();
            entity.setStatus(newStatus);
            occurrenceRepository.save(entity);
            return OccurrenceStatus.EXPIRED;
        }

        return savedStatus;
    }

    private static String toDateBucket(LocalDate date) {
        return date.getYear() + "-" + date.getMonthValue();
    }
}
