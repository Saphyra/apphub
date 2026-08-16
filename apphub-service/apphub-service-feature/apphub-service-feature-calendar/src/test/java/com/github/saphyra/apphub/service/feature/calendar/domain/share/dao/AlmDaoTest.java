package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
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
class AlmDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();

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
        given(repository.getByPrincipalAndObjectType(USER_ID, PrincipalType.USER, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));

        assertThat(underTest.getByUserIdAndObjectType(USER_ID, SharedObjectType.OCCURRENCE)).containsExactly(alm);
    }

    @Test
    void findForObjectValidated() {
        given(repository.findForObject(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE)).willReturn(Optional.of(alm));

        assertThat(underTest.findForObjectValidated(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE)).isEqualTo(alm);
    }

    @Test
    void findForObjectValidated_notFound() {
        given(repository.findForObject(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findForObjectValidated(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE));
    }

    @Test
    void getByObject() {
        given(repository.getForObject(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));

        assertThat(underTest.getByObject(USER_ID, SharedObjectType.OCCURRENCE)).containsExactly(alm);
    }

    @Test
    void deleteByUserId() {
        given(repository.getByPrincipal(USER_ID, PrincipalType.USER)).willReturn(List.of(alm));

        underTest.deleteByUserId(USER_ID);

        then(repository).should().delete(List.of(alm));
    }

    @Test
    void deleteByObject() {
        given(repository.getForObject(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));

        underTest.deleteByObject(USER_ID, SharedObjectType.OCCURRENCE);

        then(repository).should().delete(List.of(alm));
    }

    @Test
    void delete() {
        underTest.delete(alm);

        then(repository).should().delete(List.of(alm));
    }
}