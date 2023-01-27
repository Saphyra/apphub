package com.github.saphyra.apphub.service.diary.dao.occurance;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OccurrenceDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final String OCCURRENCE_ID_STRING = "occurrence-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private OccurrenceRepository repository;

    @Mock
    private OccurrenceConverter converter;

    @InjectMocks
    private OccurrenceDao underTest;

    @Mock
    private OccurrenceEntity entity;

    @Mock
    private Occurrence occurrence;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void getByEventId() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.getByEventId(EVENT_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.getByEventId(EVENT_ID);

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(occurrence));

        List<Occurrence> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(repository.findById(OCCURRENCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(occurrence));

        Optional<Occurrence> result = underTest.findById(OCCURRENCE_ID);

        assertThat(result).contains(occurrence);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(repository.findById(OCCURRENCE_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(OCCURRENCE_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(OCCURRENCE_ID)).willReturn(OCCURRENCE_ID_STRING);
        given(repository.findById(OCCURRENCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(occurrence));

        Occurrence result = underTest.findByIdValidated(OCCURRENCE_ID);

        assertThat(result).isEqualTo(occurrence);
    }

    @Test
    public void deleteByEventId() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);

        underTest.deleteByEventId(EVENT_ID);

        verify(repository).deleteByEventId(EVENT_ID_STRING);
    }
}