package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelObjectQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OWN_LABEL_ID = UUID.randomUUID();
    private static final UUID SHARED_LABEL_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private LabelDao labelDao;

    @InjectMocks
    private LabelObjectQueryService underTest;

    @Mock
    private Label ownLabel;

    @Mock
    private Label sharedLabel;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Mock
    private Alm alm;

    @Captor
    private ArgumentCaptor<List<BiWrapper<UUID, UUID>>> argumentCaptor;

    @Test
    void getLabelsOfEvent() {
        given(eventLabelMappingDao.getLabelsOfEvent(USER_ID, EVENT_ID)).willReturn(eventLabelMapping);
        given(eventLabelMapping.getLabelIds()).willReturn(Map.of(OWN_LABEL_ID, USER_ID, SHARED_LABEL_ID, OTHER_USER_ID));
        given(almDao.findForObject(USER_ID, PrincipalType.USER, SHARED_LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.VIEW));
        given(labelDao.getByIds(anyList())).willReturn(List.of(ownLabel, sharedLabel));

        assertThat(underTest.getLabelsOfEvent(USER_ID, EVENT_ID)).containsExactlyInAnyOrder(ownLabel, sharedLabel);

        then(labelDao).should().getByIds(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(new BiWrapper<>(USER_ID, OWN_LABEL_ID), new BiWrapper<>(OTHER_USER_ID, SHARED_LABEL_ID));
    }

    @Test
    void findLabel_own() {
        given(labelDao.findById(USER_ID, OWN_LABEL_ID)).willReturn(Optional.of(ownLabel));

        assertThat(underTest.findLabel(USER_ID, OWN_LABEL_ID, Grant.VIEW)).contains(ownLabel);
    }

    @Test
    void findLabel_shared() {
        given(labelDao.findById(USER_ID, SHARED_LABEL_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(USER_ID, PrincipalType.USER, SHARED_LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.VIEW));
        given(alm.getOwner()).willReturn(OTHER_USER_ID);
        given(alm.getObjectId()).willReturn(SHARED_LABEL_ID);
        given(labelDao.findByIdValidated(OTHER_USER_ID, SHARED_LABEL_ID)).willReturn(sharedLabel);

        assertThat(underTest.findLabel(USER_ID, SHARED_LABEL_ID, Grant.VIEW)).contains(sharedLabel);
    }

    @Test
    void findLabel_shared_noGrant() {
        given(labelDao.findById(USER_ID, SHARED_LABEL_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(USER_ID, PrincipalType.USER, SHARED_LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of());

        assertThat(underTest.findLabel(USER_ID, SHARED_LABEL_ID, Grant.VIEW)).isEmpty();
    }

    @Test
    void getByUserId(){
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(ownLabel));
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.LABEL)).willReturn(List.of(alm));
        given(alm.getOwner()).willReturn(OTHER_USER_ID);
        given(alm.getObjectId()).willReturn(SHARED_LABEL_ID);
        given(alm.getGrants()).willReturn(Set.of(Grant.VIEW));
        given(labelDao.findByIdValidated(OTHER_USER_ID, SHARED_LABEL_ID)).willReturn(sharedLabel);

        assertThat(underTest.getByUserId(USER_ID)).containsExactlyInAnyOrder(
            new BiWrapper<>(ownLabel, Grant.forType(SharedObjectType.LABEL)),
            new BiWrapper<>(sharedLabel, Set.of(Grant.VIEW))
        );
    }
}