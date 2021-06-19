package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetTest {
    @Mock
    private Surface surface;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Building building;

    @Test
    public void getBuildings() {
        Planet underTest = Planet.builder()
            .surfaces(CollectionUtils.singleValueMap(coordinate, surface))
            .build();
        given(surface.getBuilding()).willReturn(building);

        List<Building> result = underTest.getBuildings();

        assertThat(result).containsExactly(building);
    }
}