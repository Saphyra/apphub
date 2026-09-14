package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AlmDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();

    @Mock
    private AlmRepository repository;

    @Mock
    private PrincipalAlmCache principalAlmCache;

    @Mock
    private ObjectAlmCache objectAlmCache;

    @InjectMocks
    private AlmDao underTest;

    @Mock
    private Alm alm;

    @Test
    void save() {
        given(alm.getPrincipal()).willReturn(USER_ID);
        given(alm.getObjectId()).willReturn(OBJECT_ID);

        underTest.save(alm);

        then(repository).should().save(alm);
        then(principalAlmCache).should().invalidate(USER_ID);
        then(objectAlmCache).should().invalidate(OBJECT_ID);
    }

    @Test
    void getByUserId() {
        given(principalAlmCache.get(eq(USER_ID), any())).willReturn(Map.of(OBJECT_ID, alm));
        given(repository.getByPrincipal(USER_ID, PrincipalType.USER)).willReturn(List.of(alm));
        given(alm.getObjectId()).willReturn(OBJECT_ID);

        assertThat(underTest.getByUserId(USER_ID)).containsEntry(OBJECT_ID, alm);

        ArgumentCaptor<Supplier<Map<UUID, Alm>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(principalAlmCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(OBJECT_ID, alm);
    }

    @Test
    void getByUserIdAndObjectType() {
        given(principalAlmCache.get(eq(USER_ID), any())).willReturn(Map.of(OBJECT_ID, alm));
        given(alm.getObjectType()).willReturn(SharedObjectType.OCCURRENCE);

        assertThat(underTest.getByUserIdAndObjectType(USER_ID, SharedObjectType.OCCURRENCE)).containsExactly(alm);
    }

    @Test
    void findForObjectValidated() {
        given(principalAlmCache.get(eq(USER_ID), any())).willReturn(Map.of(USER_ID, alm));

        assertThat(underTest.findSharedObjectValidated(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE)).isEqualTo(alm);
    }

    @Test
    void findForObjectValidated_notFound() {
        given(principalAlmCache.get(eq(USER_ID), any())).willReturn(Map.of());

        ExceptionValidator.validateNotFoundException(() -> underTest.findSharedObjectValidated(USER_ID, PrincipalType.USER, USER_ID, SharedObjectType.OCCURRENCE));
    }

    @Test
    void getByObject() {
        given(objectAlmCache.get(eq(USER_ID), any())).willReturn(Map.of(USER_ID, alm));
        given(alm.getPrincipal()).willReturn(USER_ID);
        given(repository.getForObjectId(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));

        assertThat(underTest.getByObjectId(USER_ID, SharedObjectType.OCCURRENCE)).containsEntry(USER_ID, alm);

        ArgumentCaptor<Supplier<Map<UUID, Alm>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(objectAlmCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(USER_ID, alm);
    }

    @Test
    void deleteByUserId() {
        Map<UUID, Alm> map = Map.of(OBJECT_ID, alm);
        given(principalAlmCache.get(eq(USER_ID), any())).willReturn(map);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().delete(map.values());
        then(principalAlmCache).should().invalidate(USER_ID);
        then(objectAlmCache).should().invalidateAll(Set.of(OBJECT_ID));
    }

    @Test
    void deleteByObject() {
        Map<UUID, Alm> map = Map.of(USER_ID, alm);
        given(objectAlmCache.get(eq(OBJECT_ID), any())).willReturn(map);

        underTest.deleteByObject(OBJECT_ID, SharedObjectType.OCCURRENCE);

        then(repository).should().delete(map.values());
        then(objectAlmCache).should().invalidate(OBJECT_ID);
        then(principalAlmCache).should().invalidateAll(Set.of(USER_ID));
    }

    @Test
    void delete() {
        underTest.delete(alm);

        then(repository).should().delete(List.of(alm));
    }
}