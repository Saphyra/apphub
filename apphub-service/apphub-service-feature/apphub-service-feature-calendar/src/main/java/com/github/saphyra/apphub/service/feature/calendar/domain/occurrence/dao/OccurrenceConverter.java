package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
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

import java.time.LocalDate;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMINDED;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_REMIND_ME_BEFORE_DAYS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_STATUS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_TIME;

@Component
@RequiredArgsConstructor
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    private final UuidConverter uuidConverter;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final DateTimeUtil dateTimeUtil;
    private final OccurrenceRepository occurrenceRepository;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String occurrenceId = uuidConverter.convertDomain(domain.getOccurrenceId());

        return OccurrenceEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .occurrenceId(occurrenceId)
            .dateBucket(toDateBucket(domain.getDate()))
            .date(localDateEncryptor.encrypt(domain.getDate(), userId, occurrenceId, COLUMN_DATE))
            .time(localTimeEncryptor.encrypt(domain.getTime(), userId, occurrenceId, COLUMN_TIME))
            .status(stringEncryptor.encrypt(domain.getStatus().name(), userId, occurrenceId, COLUMN_STATUS))
            .note(stringEncryptor.encrypt(domain.getNote(), userId, occurrenceId, COLUMN_NOTE))
            .remindMeBeforeDays(integerEncryptor.encrypt(domain.getRemindMeBeforeDays(), userId, occurrenceId, COLUMN_REMIND_ME_BEFORE_DAYS))
            .reminded(booleanEncryptor.encrypt(domain.isReminded(), userId, occurrenceId, COLUMN_REMINDED))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();


        LocalDate date = localDateEncryptor.decrypt(entity.getDate(), userId, entity.getOccurrenceId(), COLUMN_DATE);
        return Occurrence.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .date(date)
            .time(localTimeEncryptor.decrypt(entity.getTime(), userId, entity.getOccurrenceId(), COLUMN_TIME))
            .status(syncStatus(entity, userId, date))
            .note(stringEncryptor.decrypt(entity.getNote(), userId, entity.getOccurrenceId(), COLUMN_NOTE))
            .remindMeBeforeDays(integerEncryptor.decrypt(entity.getRemindMeBeforeDays(), userId, entity.getOccurrenceId(), COLUMN_REMIND_ME_BEFORE_DAYS))
            .reminded(booleanEncryptor.decrypt(entity.getReminded(), userId, entity.getOccurrenceId(), COLUMN_REMINDED))
            .build();
    }

    private OccurrenceStatus syncStatus(OccurrenceEntity entity, String userId, LocalDate date) {
        OccurrenceStatus savedStatus = OccurrenceStatus.valueOf(stringEncryptor.decrypt(entity.getStatus(), userId, entity.getOccurrenceId(), COLUMN_STATUS));

        if (savedStatus == OccurrenceStatus.PENDING && dateTimeUtil.getCurrentDate().isAfter(date)) {
            String encryptedStatus = stringEncryptor.encrypt(OccurrenceStatus.EXPIRED.name(), userId, entity.getOccurrenceId(), COLUMN_STATUS);
            entity.setStatus(encryptedStatus);
            occurrenceRepository.save(entity);
            return OccurrenceStatus.EXPIRED;
        }

        return savedStatus;
    }

    private static String toDateBucket(LocalDate date) {
        return date.getYear() + "-" + date.getMonthValue();
    }
}
