package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingFactoryTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int LEVEL = 324;
    private static final UUID LOCATION = UUID.randomUUID();
    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BuildingFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(BUILDING_ID);

        Building result = underTest.create(DATA_ID, LOCATION, SURFACE_ID, LEVEL);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }
}