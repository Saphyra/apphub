package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EmptySurfaceMapFactoryTest {
    @InjectMocks
    private EmptySurfaceMapFactory underTest;

    @Test
    public void createEmptySurfaceMap() {
        SurfaceType[][] result = underTest.createEmptySurfaceMap(2);

        assertThat(result).hasSize(2);
        assertThat(result[0]).hasSize(2);
        assertThat(result[1]).hasSize(2);
    }
}