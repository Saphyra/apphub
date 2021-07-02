package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SurfaceDao surfaceDao;

    @Mock
    private SurfaceModelValidator surfaceModelValidator;

    @InjectMocks
    private SurfaceService underTest;

    @Mock
    private SurfaceModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(surfaceDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SURFACE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(surfaceModelValidator).validate(model);
        verify(surfaceDao).saveAll(Arrays.asList(model));
    }
}