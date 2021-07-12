package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
public class UniverseServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private UniverseDao universeDao;

    @Mock
    private UniverseModelValidator universeModelValidator;

    @InjectMocks
    private UniverseService underTest;

    @Mock
    private UniverseModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(universeDao).deleteById(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.UNIVERSE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(universeModelValidator).validate(model);
        verify(universeDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(universeDao.findById(ID)).willReturn(Optional.of(model));

        Optional<UniverseModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent_notFound() {
        given(universeDao.findById(ID)).willReturn(Optional.empty());

        List<UniverseModel> result = underTest.getByParent(ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void getByParent() {
        given(universeDao.findById(ID)).willReturn(Optional.of(model));

        List<UniverseModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }
}