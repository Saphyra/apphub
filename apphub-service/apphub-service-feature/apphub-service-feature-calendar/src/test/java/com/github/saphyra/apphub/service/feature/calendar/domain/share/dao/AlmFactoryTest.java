package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AlmFactoryTest {
    private static final UUID PRINCIPAL = UUID.randomUUID();
    private static final UUID OBJECT_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();

    @InjectMocks
    private AlmFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(PRINCIPAL, PrincipalType.USER, OBJECT_ID, SharedObjectType.OCCURRENCE, OWNER, PARENT, Set.of(Grant.DELETE)))
            .returns(PRINCIPAL, Alm::getPrincipal)
            .returns(PrincipalType.USER, Alm::getPrincipalType)
            .returns(OBJECT_ID, Alm::getObjectId)
            .returns(SharedObjectType.OCCURRENCE, Alm::getObjectType)
            .returns(OWNER, Alm::getOwner)
            .returns(PARENT, Alm::getParent)
            .returns(Set.of(Grant.DELETE), Alm::getGrants);
    }
}