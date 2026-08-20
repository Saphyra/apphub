package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceGrantFinderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private OccurrenceGrantFinder underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Alm alm;

    @Test
    void ownOccurrence() {
        given(occurrenceDao.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getUserId()).willReturn(USER_ID);

        assertThat(underTest.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(occurrence, BiWrapper::getEntity1)
            .returns(Grant.forType(SharedObjectType.OCCURRENCE), BiWrapper::getEntity2);
    }

    @Test
    void sharedOccurrence() {
        given(occurrenceDao.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).willReturn(occurrence);
        given(occurrence.getUserId()).willReturn(UUID.randomUUID());
        given(almDao.findForObject(USER_ID, PrincipalType.USER, OCCURRENCE_ID, SharedObjectType.OCCURRENCE)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.SEE));

        assertThat(underTest.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(occurrence, BiWrapper::getEntity1)
            .returns(Set.of(Grant.SEE), BiWrapper::getEntity2);
    }
}