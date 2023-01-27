package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UniverseLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer SIZE = 234;

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private SystemConnectionLoader systemConnectionLoader;

    @Mock
    private SolarSystemLoader solarSystemLoader;

    @InjectMocks
    private UniverseLoader underTest;

    @Mock
    private UniverseModel universeModel;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private SystemConnection systemConnection;

    @Test
    public void load_notFound() {
        Throwable ex = catchThrowable(() -> underTest.load(GAME_ID));

        ExceptionValidator.validateReportedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void load() {
        given(gameItemLoader.loadItem(GAME_ID, GameItemType.UNIVERSE)).willReturn(Optional.of(universeModel));

        given(universeModel.getSize()).willReturn(SIZE);
        given(solarSystemLoader.load(GAME_ID)).willReturn(CollectionUtils.singleValueMap(coordinate, solarSystem));
        given(systemConnectionLoader.load(GAME_ID)).willReturn(Arrays.asList(systemConnection));

        Universe result = underTest.load(GAME_ID);

        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getSystems()).containsEntry(coordinate, solarSystem);
        assertThat(result.getConnections()).containsExactly(systemConnection);
    }
}