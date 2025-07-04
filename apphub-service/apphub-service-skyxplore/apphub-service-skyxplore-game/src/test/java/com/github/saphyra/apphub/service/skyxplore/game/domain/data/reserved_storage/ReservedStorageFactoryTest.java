package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
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
public class ReservedStorageFactoryTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 234;
    private static final UUID CONTAINER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ReservedStorageFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(RESERVED_STORAGE_ID);

        ReservedStorage result = underTest.create(CONTAINER_ID, ContainerType.STORAGE, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result.getReservedStorageId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(result.getContainerId()).isEqualTo(CONTAINER_ID);
        assertThat(result.getContainerType()).isEqualTo(ContainerType.STORAGE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}