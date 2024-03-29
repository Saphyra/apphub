package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AllocatedResourceFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 534;
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private AllocatedResourceFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(ALLOCATED_RESOURCE_ID);

        AllocatedResource result = underTest.create(LOCATION, EXTERNAL_REFERENCE, DATA_ID, AMOUNT);

        assertThat(result.getAllocatedResourceId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}