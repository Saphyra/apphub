package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private SolarSystemDao solarSystemDao;

    @Mock
    private SolarSystemModelValidator solarSystemModelValidator;

    @InjectMocks
    private SolarSystemService underTest;

    @Mock
    private SolarSystemModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(solarSystemDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(solarSystemModelValidator).validate(model);
        verify(solarSystemDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(solarSystemDao.findById(ID)).willReturn(Optional.of(model));

        Optional<SolarSystemModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(solarSystemDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(model));

        List<SolarSystemModel> result = underTest.getByParent(GAME_ID);

        assertThat(result).containsExactly(model);
    }
}