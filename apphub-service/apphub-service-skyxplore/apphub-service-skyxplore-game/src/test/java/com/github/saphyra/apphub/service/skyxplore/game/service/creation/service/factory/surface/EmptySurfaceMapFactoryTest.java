package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class EmptySurfaceMapFactoryTest {
    @InjectMocks
    private EmptySurfaceMapFactory underTest;

    @Test
    public void createEmptySurfaceMap() {
        SurfaceType[][] result = underTest.createEmptySurfaceMap(2);

        assertThat(result).hasNumberOfRows(2);
        assertThat(result[0]).hasSize(2);
        assertThat(result[1]).hasSize(2);
    }
}