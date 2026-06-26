package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganizationFactoryTest {
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private OrganizationFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ORGANIZATION_ID);

        assertThat(underTest.create(NAME, DESCRIPTION))
            .returns(ORGANIZATION_ID, Organization::getId)
            .returns(NAME, Organization::getName)
            .returns(DESCRIPTION, Organization::getDescription);
    }
}