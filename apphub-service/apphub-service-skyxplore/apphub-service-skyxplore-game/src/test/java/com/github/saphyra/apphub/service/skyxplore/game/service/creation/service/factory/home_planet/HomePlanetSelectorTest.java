package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class HomePlanetSelectorTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AllianceMemberSystemFinder allianceMemberSystemFinder;

    @Mock
    private RandomEmptyPlanetFinder randomEmptyPlanetFinder;

    @InjectMocks
    private HomePlanetSelector underTest;

    @Mock
    private Alliance alliance;

    @Mock
    private Universe universe;

    @Mock
    private Player player;

    @Mock
    private Planet planet;

    @Test
    @Ignore //TODO restore when home planet finder bug is fixed
    public void selectPlanet_inAlliance() {
        given(alliance.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, player));
        given(player.getUserId()).willReturn(USER_ID);
        given(allianceMemberSystemFinder.findAllianceMemberSystem(Arrays.asList(USER_ID), universe)).willReturn(planet);

        Planet result = underTest.selectPlanet(USER_ID, Arrays.asList(alliance), universe);

        assertThat(result).isEqualTo(planet);
    }

    @Test
    public void selectPlanet_notInAlliance() {
        //given(alliance.getMembers()).willReturn(Collections.emptyMap());
        given(randomEmptyPlanetFinder.randomEmptyPlanet(universe)).willReturn(planet);

        Planet result = underTest.selectPlanet(USER_ID, Arrays.asList(alliance), universe);

        assertThat(result).isEqualTo(planet);
    }
}