package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetOverviewResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PlanetOverviewControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String NEW_PLANET_NAME = "new-planet-name";

    @Mock
    private PlanetOverviewQueryService planetOverviewQueryService;

    @Mock
    private RenamePlanetService renamePlanetService;

    @InjectMocks
    private PlanetOverviewControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private PlanetOverviewResponse planetOverviewResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getOverview() {
        given(planetOverviewQueryService.getOverview(USER_ID, PLANET_ID)).willReturn(planetOverviewResponse);

        PlanetOverviewResponse result = underTest.getPlanetOverview(PLANET_ID, accessTokenHeader);

        assertThat(result).isEqualTo(planetOverviewResponse);
    }

    @Test
    public void renamePlanet() {
        underTest.renamePlanet(new OneParamRequest<>(NEW_PLANET_NAME), PLANET_ID, accessTokenHeader);

        verify(renamePlanetService).rename(USER_ID, PLANET_ID, NEW_PLANET_NAME);
    }
}