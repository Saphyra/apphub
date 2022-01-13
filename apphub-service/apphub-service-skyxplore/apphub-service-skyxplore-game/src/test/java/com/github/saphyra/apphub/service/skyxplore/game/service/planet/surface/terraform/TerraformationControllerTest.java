package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ToString
@RunWith(MockitoJUnitRunner.class)
public class TerraformationControllerTest {
    private static final String SURFACE_TYPE = "surface-type";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private TerraformationService terraformationService;

    @Mock
    private CancelTerraformationService cancelTerraformationService;

    @InjectMocks
    private TerraformationController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void terraformSurface() {
        given(terraformationService.terraform(USER_ID, PLANET_ID, SURFACE_ID, SURFACE_TYPE)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.terraformSurface(new OneParamRequest<>(SURFACE_TYPE), PLANET_ID, SURFACE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(result);
    }

    @Test
    public void cancelTerraformation() {
        given(cancelTerraformationService.cancelTerraformationOfSurface(USER_ID, PLANET_ID, SURFACE_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.cancelTerraformation(PLANET_ID, SURFACE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(result);
    }
}