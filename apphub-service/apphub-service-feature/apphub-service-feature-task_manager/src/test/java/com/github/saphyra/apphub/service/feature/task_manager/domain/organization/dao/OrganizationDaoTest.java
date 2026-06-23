package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

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
class OrganizationDaoTest {
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private OrganizationDao underTest;

    @Mock
    private Organization organization;

    @Test
    void save() {
        underTest.save(organization);

        then(repository).should().save(organization);
    }

    @Test
    void getByIds() {
        given(repository.getByIds(List.of(ORGANIZATION_ID))).willReturn(List.of(organization));

        assertThat(underTest.getByIds(List.of(ORGANIZATION_ID))).containsExactly(organization);
    }

    @Test
    void findByIdValidated() {
        given(repository.findById(ORGANIZATION_ID)).willReturn(java.util.Optional.of(organization));

        assertThat(underTest.findByIdValidated(ORGANIZATION_ID)).isEqualTo(organization);
    }

    @Test
    void findByIdValidated_notFound() {
        given(repository.findById(ORGANIZATION_ID)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(ORGANIZATION_ID));
    }

    @Test
    void delete() {
        underTest.delete(ORGANIZATION_ID);

        then(repository).should().delete(ORGANIZATION_ID);
    }
}