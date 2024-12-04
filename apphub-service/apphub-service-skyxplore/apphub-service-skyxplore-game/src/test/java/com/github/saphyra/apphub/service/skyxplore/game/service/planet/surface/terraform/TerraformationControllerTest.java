package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ToString
@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void terraformSurface() {
        underTest.terraformSurface(new OneParamRequest<>(SURFACE_TYPE), PLANET_ID, SURFACE_ID, accessTokenHeader);

        verify(terraformationService).terraform(USER_ID, PLANET_ID, SURFACE_ID, SURFACE_TYPE);
    }

    @Test
    public void cancelTerraformation() {
        underTest.cancelTerraformation(PLANET_ID, SURFACE_ID, accessTokenHeader);

        verify(cancelTerraformationService).cancelTerraformationOfSurface(USER_ID, SURFACE_ID);
    }
}