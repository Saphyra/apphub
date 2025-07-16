package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalTimeEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_REMIND_AT = "remind_at";
    private static final String COLUMN_REMINDED = "reminded";

    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final StringEncryptor stringEncryptor;
    private final BooleanEncryptor booleanEncryptor;

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
            .remindAt(localDateEncryptor.encrypt(domain.getRemindAt(), userIdFromAccessToken, occurrenceId, COLUMN_REMIND_AT))
            .reminded(booleanEncryptor.encrypt(domain.getReminded(), userIdFromAccessToken, occurrenceId, COLUMN_REMINDED))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        String userIdFromAccessToken = accessTokenProvider.getUserIdAsString();

        return Occurrence.builder()
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .date(localDateEncryptor.decrypt(entity.getDate(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_DATE))
            .time(localTimeEncryptor.decrypt(entity.getTime(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_TIME))
            .status(OccurrenceStatus.valueOf(stringEncryptor.decrypt(entity.getStatus(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_STATUS)))
            .note(stringEncryptor.decrypt(entity.getNote(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_NOTE))
            .remindAt(localDateEncryptor.decrypt(entity.getRemindAt(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_REMIND_AT))
            .reminded(booleanEncryptor.decrypt(entity.getReminded(), userIdFromAccessToken, entity.getOccurrenceId(), COLUMN_REMINDED))
            .build();
    }
}
