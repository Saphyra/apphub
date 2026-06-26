package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.service;

import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Operation;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AlmServiceTest {
    private static final UUID PRINCIPAL = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final List<Operation> OPERATIONS = List.of(Operation.OWNER, Operation.READ);

    @Mock
    private AlmFactory almFactory;

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private AlmService underTest;

    @Mock
    private Alm alm;

    @Test
    void grantOperations_existingAlm() {
        given(almDao.findForObject(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION)).willReturn(Optional.of(alm));

        underTest.grantOperations(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION, OPERATIONS);

        then(almFactory).shouldHaveNoInteractions();
        then(alm).should().addOperation(Operation.OWNER);
        then(alm).should().addOperation(Operation.READ);
        then(almDao).should().save(alm);
    }

    @Test
    void grantOperations_createAlm() {
        given(almDao.findForObject(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION)).willReturn(Optional.empty());
        given(almFactory.createAlm(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION, OPERATIONS)).willReturn(alm);

        underTest.grantOperations(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION, OPERATIONS);

        then(almFactory).should().createAlm(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION, OPERATIONS);
        then(alm).should().addOperation(Operation.OWNER);
        then(alm).should().addOperation(Operation.READ);
        then(almDao).should().save(alm);
    }
}