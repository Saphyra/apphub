package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
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
        assertThat(underTest.create(EXTERNAL_REFERENCE, CommodityType.COMMODITY, LAST_UPDATE))
            .returns(EXTERNAL_REFERENCE, LastUpdate::getExternalReference)
            .returns(EntityType.COMMODITY, LastUpdate::getType)
            .returns(LAST_UPDATE, LastUpdate::getLastUpdate);
    }
}