package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SurfaceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

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

    @Test
    public void findById() {
        given(surfaceDao.findById(ID)).willReturn(Optional.of(model));

        Optional<SurfaceModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(surfaceDao.getByPlanetId(ID)).willReturn(Arrays.asList(model));

        List<SurfaceModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(surfaceDao).deleteById(ID);
    }
}