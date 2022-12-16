package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SkyXplorePlanetPopulationControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String NEW_NAME = "new-name";
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private PopulationQueryService populationQueryService;

    @Mock
    private RenameCitizenService renameCitizenService;

    @InjectMocks
    private SkyXplorePlanetPopulationControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CitizenResponse citizenResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getPopulation() {
        given(populationQueryService.getPopulation(USER_ID, PLANET_ID)).willReturn(Arrays.asList(citizenResponse));

        List<CitizenResponse> result = underTest.getPopulation(PLANET_ID, accessTokenHeader);

        assertThat(result).containsExactly(citizenResponse);
    }

    @Test
    public void renameCitizen() {
        given(renameCitizenService.renameCitizen(USER_ID, PLANET_ID, CITIZEN_ID, NEW_NAME)).willReturn(citizenResponse);

        CitizenResponse result = underTest.renameCitizen(new OneParamRequest<>(NEW_NAME), PLANET_ID, CITIZEN_ID, accessTokenHeader);

        assertThat(result).isEqualTo(citizenResponse);
    }
}