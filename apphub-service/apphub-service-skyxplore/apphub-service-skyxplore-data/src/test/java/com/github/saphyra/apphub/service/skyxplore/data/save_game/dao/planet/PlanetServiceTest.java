package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
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
public class PlanetServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private PlanetDao planetDao;

    @Mock
    private PlanetModelValidator planetModelValidator;

    @InjectMocks
    private PlanetService underTest;

    @Mock
    private PlanetModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(planetDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PLANET);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(planetModelValidator).validate(model);
        verify(planetDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(planetDao.findById(ID)).willReturn(Optional.of(model));

        Optional<PlanetModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByModel() {
        given(planetDao.getBySolarSystemId(ID)).willReturn(Arrays.asList(model));

        List<PlanetModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(planetDao).deleteById(ID);
    }
}