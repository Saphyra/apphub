package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CoordinateLoaderTest {
    private static final UUID COORDINATE_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private CoordinateLoader underTest;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void loadById_notFound() {
        assertThat(underTest.loadById(COORDINATE_ID)).isNull();
    }

    @Test
    public void loadById() {
        given(gameItemLoader.loadItem(COORDINATE_ID, GameItemType.COORDINATE)).willReturn(Optional.of(coordinateModel));

        assertThat(underTest.loadById(COORDINATE_ID)).isEqualTo(coordinateModel);
    }

    @Test
    public void loadOneByReferenceId_notFound() {
        assertThat(underTest.loadOneByReferenceId(REFERENCE_ID)).isNull();
    }

    @Test
    public void loadOneByReferenceId() {
        given(gameItemLoader.loadChildren(REFERENCE_ID, GameItemType.COORDINATE, CoordinateModel[].class)).willReturn(Arrays.asList(coordinateModel));

        assertThat(underTest.loadOneByReferenceId(REFERENCE_ID)).isEqualTo(coordinateModel);
    }
}