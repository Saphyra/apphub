package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UnshareObjectServiceTest {
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private UnshareObjectService underTest;

    @Mock
    private Alm alm;

    @Test
    void unshareOwnAlm() {
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, OBJECT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm));
        given(alm.getOwner()).willReturn(USER_ID);

        underTest.unshareObject(USER_ID, SHARED_WITH, SharedObjectType.EVENT, OBJECT_ID);

        then(almDao).should().delete(alm);
    }

    @Test
    void unshareOthersAlm() {
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, OBJECT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm));
        given(alm.getOwner()).willReturn(UUID.randomUUID());

        underTest.unshareObject(USER_ID, SHARED_WITH, SharedObjectType.EVENT, OBJECT_ID);

        then(almDao).should(never()).delete(any());
    }
}