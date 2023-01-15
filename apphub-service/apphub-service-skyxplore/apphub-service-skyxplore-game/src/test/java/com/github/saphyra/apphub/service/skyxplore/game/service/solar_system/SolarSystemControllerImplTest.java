package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
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
public class SolarSystemControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String NEW_SOLAR_SYSTEM_NAME = "new-solar-system-name";

    @Mock
    private SolarSystemResponseQueryService solarSystemResponseQueryService;

    @Mock
    private RenameSolarSystemService renameSolarSystemService;

    @InjectMocks
    private SolarSystemControllerImpl underTest;

    @Mock
    private SolarSystemResponse solarSystemResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getSolarSystem() {
        given(solarSystemResponseQueryService.getSolarSystem(USER_ID, SOLAR_SYSTEM_ID)).willReturn(solarSystemResponse);

        SolarSystemResponse result = underTest.getSolarSystem(SOLAR_SYSTEM_ID, accessTokenHeader);

        assertThat(result).isEqualTo(solarSystemResponse);
    }

    @Test
    public void renameSolarSystem() {
        underTest.renameSolarSystem(new OneParamRequest<>(NEW_SOLAR_SYSTEM_NAME), SOLAR_SYSTEM_ID, accessTokenHeader);

        verify(renameSolarSystemService).rename(USER_ID, SOLAR_SYSTEM_ID, NEW_SOLAR_SYSTEM_NAME);
    }
}