package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OccurrenceDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final String OCCURRENCE_ID_STRING = "occurrence-id";
    private static final String BUCKET = "2026-05";
    private static final String BUCKET_2 = "2026-6";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private OccurrenceConverter converter;

    @Mock
    private OccurrenceRepository repository;

    @InjectMocks
    private OccurrenceDao underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private OccurrenceEntity entity;
    @Test
    void save() {
        given(converter.convertDomain(occurrence)).willReturn(entity);

        underTest.save(occurrence);

        then(repository).should().save(entity);
    }

    @Test
    void getByEventId() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.getByEventId(EVENT_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(occurrence));

        assertThat(underTest.getByEventId(EVENT_ID)).containsExactly(occurrence);
    }

    @Test
    void delete_partitionsOccurrenceIds() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(List.of(OCCURRENCE_ID))).willReturn(List.of(OCCURRENCE_ID_STRING));

        underTest.delete(EVENT_ID, List.of(OCCURRENCE_ID));

        then(repository).should().delete(EVENT_ID_STRING, List.of(OCCURRENCE_ID_STRING));
    }

    @Test
    void save_partitionsOccurrences() {
        given(converter.convertDomain(List.of(occurrence))).willReturn(List.of(entity));

        underTest.save(List.of(occurrence));

        then(repository).should().save(List.of(entity));
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(repository.findById(EVENT_ID_STRING, OCCURRENCE_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(EVENT_ID, OCCURRENCE_ID));
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(repository.findById(EVENT_ID_STRING, OCCURRENCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(occurrence));

        assertThat(underTest.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).isEqualTo(occurrence);
    }

    @Test
    void getByBuckets() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByBucket(USER_ID_STRING, BUCKET)).willReturn(List.of(entity));
        given(converter.convertEntity(entity)).willReturn(occurrence);

        assertThat(underTest.getByBuckets(USER_ID, List.of(BUCKET, BUCKET_2))).containsExactly(occurrence);
    }

    @Test
    void deleteOccurrences() {
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);

        underTest.delete(List.of(occurrence));

        then(repository).should().delete(List.of(new BiWrapper<>(EVENT_ID_STRING, OCCURRENCE_ID_STRING)));
    }

    @Test
    void deleteByEventId() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.getByEventId(EVENT_ID_STRING)).willReturn(List.of(entity));
        given(entity.getOccurrenceId()).willReturn(OCCURRENCE_ID_STRING);

        underTest.deleteByEventId(EVENT_ID);

        then(repository).should().delete(EVENT_ID_STRING, List.of(OCCURRENCE_ID_STRING));
    }
}
