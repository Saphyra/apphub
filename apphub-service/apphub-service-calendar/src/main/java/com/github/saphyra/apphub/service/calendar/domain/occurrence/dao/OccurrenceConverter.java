package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_REMIND_ME_BEFORE_DAYS = "remind_me_before_days";
    private static final String COLUMN_REMINDED = "reminded";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final DateTimeUtil dateTimeUtil;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();
        String occurrenceId = uuidConverter.convertDomain(domain.getOccurrenceId());

        return OccurrenceEntity.builder()
            .occurrenceId(occurrenceId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .date(localDateEncryptor.encrypt(domain.getDate(), userIdFromAccessToken, occurrenceId, COLUMN_DATE))
            .time(localTimeEncryptor.encrypt(domain.getTime(), userIdFromAccessToken, occurrenceId, COLUMN_TIME))
            .status(stringEncryptor.encrypt(domain.getStatus().name(), userIdFromAccessToken, occurrenceId, COLUMN_STATUS))
            .note(stringEncryptor.encrypt(domain.getNote(), userIdFromAccessToken, occurrenceId, COLUMN_NOTE))
            .remindMeBeforeDays(integerEncryptor.encrypt(domain.getRemindMeBeforeDays(), userIdFromAccessToken, occurrenceId, COLUMN_REMIND_ME_BEFORE_DAYS))
            .reminded(booleanEncryptor.encrypt(domain.getReminded(), userIdFromAccessToken, occurrenceId, COLUMN_REMINDED))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();

        LocalDate occurrenceDate = localDateEncryptor.decrypt(entity.getDate(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_DATE);
        return Occurrence.builder()
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .date(occurrenceDate)
            .time(localTimeEncryptor.decrypt(entity.getTime(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_TIME))
            .status(getStatus(entity, userIdFromAccessToken, occurrenceDate))
            .note(stringEncryptor.decrypt(entity.getNote(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_NOTE))
            .remindMeBeforeDays(integerEncryptor.decrypt(entity.getRemindMeBeforeDays(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_REMIND_ME_BEFORE_DAYS))
            .reminded(booleanEncryptor.decrypt(entity.getReminded(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_REMINDED))
            .build();
    }

    //TODO set status to EXPIRED if date is in the past and status is PENDING
    private OccurrenceStatus getStatus(OccurrenceEntity entity, String userIdFromAccessToken, LocalDate occurrenceDate) {
        OccurrenceStatus savedStatus = OccurrenceStatus.valueOf(stringEncryptor.decrypt(entity.getStatus(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_STATUS));

        if (savedStatus == OccurrenceStatus.PENDING && dateTimeUtil.getCurrentDate().isAfter(occurrenceDate)) {
            return OccurrenceStatus.EXPIRED;
        }

        return savedStatus;
    }
}
