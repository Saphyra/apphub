package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;

@RunWith(MockitoJUnitRunner.class)
public class ReservedStorageFactoryTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 234;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ReservedStorageFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(RESERVED_STORAGE_ID);

        ReservedStorage result = underTest.create(EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result.getReservedStorageId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}