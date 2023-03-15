package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceMapTest {
    private static final UUID ID = UUID.randomUUID();

    private SurfaceMap underTest;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @BeforeEach
    public void setUp() {
        underTest = new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface));
    }

    @Test
    public void findByIdValidated_notFound() {
        given(surface.getSurfaceId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(surface.getSurfaceId()).willReturn(ID);

        Surface result = underTest.findByIdValidated(ID);

        assertThat(result).isEqualTo(surface);
    }

    @Test
    public void findByBuildingIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByBuildingIdValidated(ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByBuildingIdValidated() {
        given(surface.getBuilding()).willReturn(building);
        given(building.getBuildingId()).willReturn(ID);

        Surface result = underTest.findByBuildingIdValidated(ID);

        assertThat(result).isEqualTo(surface);
    }
}