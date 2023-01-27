package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InhabitantFinderTest {
    private static final UUID PLAYER = UUID.randomUUID();

    @InjectMocks
    private InhabitantFinder underTest;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet1;

    @Mock
    private Planet planet2;

    @Test
    public void getInhabitants() {
        given(solarSystem.getPlanets()).willReturn(CollectionUtils.toMap(new BiWrapper<>(UUID.randomUUID(), planet1), new BiWrapper<>(UUID.randomUUID(), planet2)));
        given(planet1.getOwner()).willReturn(null);
        given(planet2.getOwner()).willReturn(PLAYER);

        List<UUID> result = underTest.getInhabitants(solarSystem);

        assertThat(result).containsExactly(PLAYER);
    }
}