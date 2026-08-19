package com.github.saphyra.apphub.service.feature.calendar.domain.share.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditSharedOperationsServiceTest {
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private EditSharedOperationsService underTest;

    @Mock
    private Alm alm;

    @Test
    void editSharedOperations() {
        given(almDao.findForObjectValidated(SHARED_WITH, PrincipalType.USER, OBJECT_ID, SharedObjectType.EVENT)).willReturn(alm);
        given(alm.getOwner()).willReturn(USER_ID);

        underTest.editSharedOperations(USER_ID, SHARED_WITH, SharedObjectType.EVENT, OBJECT_ID, Set.of(Grant.DELETE));

        then(alm).should().setGrants(Set.of(Grant.DELETE));
        then(almDao).should().save(alm);
    }

    @Test
    void editSharedOperations_notOwner() {
        given(almDao.findForObjectValidated(SHARED_WITH, PrincipalType.USER, OBJECT_ID, SharedObjectType.EVENT)).willReturn(alm);
        given(alm.getOwner()).willReturn(UUID.randomUUID());

        ExceptionValidator.validateForbiddenOperation(() -> underTest.editSharedOperations(USER_ID, SHARED_WITH, SharedObjectType.EVENT, OBJECT_ID, Set.of(Grant.DELETE)));
    }
}