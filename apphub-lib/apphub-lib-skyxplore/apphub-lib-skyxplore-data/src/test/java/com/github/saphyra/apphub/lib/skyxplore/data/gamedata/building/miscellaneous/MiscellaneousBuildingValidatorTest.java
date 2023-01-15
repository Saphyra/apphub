package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MiscellaneousBuildingValidatorTest {
    private static final String KEY = "key";

    @Mock
    private BuildingDataValidator buildingDataValidator;

    @InjectMocks
    private MiscellaneousBuildingValidator underTest;

    @Mock
    private MiscellaneousBuilding miscellaneousBuilding;

    @AfterEach
    public void validate() {
        verify(buildingDataValidator).validate(miscellaneousBuilding);
    }

    @Test
    public void nullPlaceableSurfaceTypes() {
        given(miscellaneousBuilding.getPlaceableSurfaceTypes()).willReturn(null);
        Map<String, MiscellaneousBuilding> map = new HashMap<>();
        map.put(KEY, miscellaneousBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nullInPlaceableSurfaceTypes() {
        given(miscellaneousBuilding.getPlaceableSurfaceTypes()).willReturn(Arrays.asList((SurfaceType) null));
        Map<String, MiscellaneousBuilding> map = new HashMap<>();
        map.put(KEY, miscellaneousBuilding);

        assertThat(catchThrowable(() -> underTest.validate(map))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void valid() {
        given(miscellaneousBuilding.getPlaceableSurfaceTypes()).willReturn(Arrays.asList(SurfaceType.CONCRETE));
        Map<String, MiscellaneousBuilding> map = new HashMap<>();
        map.put(KEY, miscellaneousBuilding);

        underTest.validate(map);
    }
}