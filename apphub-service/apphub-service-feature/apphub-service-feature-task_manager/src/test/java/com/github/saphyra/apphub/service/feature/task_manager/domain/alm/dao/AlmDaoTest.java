package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

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
class AlmDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PRINCIPAL_ID = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();

    @Mock
    private AlmRepository repository;

    @InjectMocks
    private AlmDao underTest;

    @Mock
    private Alm alm;

    @Test
    void save() {
        underTest.save(alm);

        then(repository).should().save(alm);
    }

    @Test
    void getByUserIdAndObjectType() {
        given(repository.getByPrincipalAndObjectType(USER_ID, PrincipalType.USER, ObjectType.ORGANIZATION)).willReturn(List.of(alm));

        assertThat(underTest.getByUserIdAndObjectType(USER_ID, ObjectType.ORGANIZATION)).containsExactly(alm);
    }

    @Test
    void findForObject() {
        given(repository.findForObject(PRINCIPAL_ID, PrincipalType.GROUP, OBJECT_ID, ObjectType.ORGANIZATION)).willReturn(Optional.of(alm));

        assertThat(underTest.findForObject(PRINCIPAL_ID, PrincipalType.GROUP, OBJECT_ID, ObjectType.ORGANIZATION)).contains(alm);
    }
}