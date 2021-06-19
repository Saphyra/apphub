package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private SolarSystemResponseQueryService solarSystemResponseQueryService;

    @InjectMocks
    private SolarSystemControllerImpl underTest;

    @Mock
    private SolarSystemResponse solarSystemResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void getSolarSystem() {
        given(solarSystemResponseQueryService.getSolarSystem(USER_ID, SOLAR_SYSTEM_ID)).willReturn(solarSystemResponse);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        SolarSystemResponse result = underTest.getSolarSystem(SOLAR_SYSTEM_ID, accessTokenHeader);

        assertThat(result).isEqualTo(solarSystemResponse);
    }
}