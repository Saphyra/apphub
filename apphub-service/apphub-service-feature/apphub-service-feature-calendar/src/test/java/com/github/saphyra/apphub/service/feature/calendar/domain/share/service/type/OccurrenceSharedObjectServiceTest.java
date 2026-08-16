package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceSharedObjectServiceTest {
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String DATE_STRING = "date";

    @Mock
    private OccurrenceObjectQueryService occurrenceObjectQueryService;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private OccurrenceSharedObjectService underTest;

    @Mock
    private Occurrence occurrence;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(SharedObjectType.OCCURRENCE);
    }

    @Test
    void getSharedObject() {
        given(occurrenceObjectQueryService.findOccurrence(OWNER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(null, occurrence));
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(occurrence.getUserId()).willReturn(USER_ID);
        given(occurrence.getDate()).willReturn(DATE);
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(dateTimeConverter.convertDomain(DATE)).willReturn(DATE_STRING);

        SharedObject result = underTest.getSharedObject(OWNER_ID, OCCURRENCE_ID, EVENT_ID);

        assertThat(result.objectId()).isEqualTo(OCCURRENCE_ID);
        assertThat(result.owner()).isEqualTo(USER_ID);
        assertThat(result.parent()).isEqualTo(EVENT_ID);
        assertThat(result.name()).isEqualTo(DATE_STRING);
    }
}