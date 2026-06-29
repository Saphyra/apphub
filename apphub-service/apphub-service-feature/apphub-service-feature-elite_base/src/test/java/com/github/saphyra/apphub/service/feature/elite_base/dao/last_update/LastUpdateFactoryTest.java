package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LastUpdateFactoryTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @InjectMocks
    private LastUpdateFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(EXTERNAL_REFERENCE, ObjectType.COMMODITY, LAST_UPDATE))
            .returns(EXTERNAL_REFERENCE, LastUpdate::getExternalReference)
            .returns(ObjectType.COMMODITY, LastUpdate::getType)
            .returns(LAST_UPDATE, LastUpdate::getLastUpdate);
    }
}