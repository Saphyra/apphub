package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AlmFactoryTest {
    private static final UUID PRINCIPAL = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final List<Operation> OPERATIONS = List.of(Operation.OWNER, Operation.READ);

    private final AlmFactory underTest = new AlmFactory();

    @Test
    void createAlm() {
        assertThat(underTest.createAlm(PRINCIPAL, PrincipalType.USER, OBJECT_ID, ObjectType.ORGANIZATION, OPERATIONS))
            .returns(PRINCIPAL, Alm::getPrincipal)
            .returns(PrincipalType.USER, Alm::getPrincipalType)
            .returns(OBJECT_ID, Alm::getObjectId)
            .returns(ObjectType.ORGANIZATION, Alm::getObjectType)
            .returns(OPERATIONS, Alm::getOperations);
    }
}