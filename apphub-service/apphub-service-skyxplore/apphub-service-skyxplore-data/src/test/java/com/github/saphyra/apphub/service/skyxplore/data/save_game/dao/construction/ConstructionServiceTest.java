package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class ConstructionServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private ConstructionDao constructionDao;

    @Mock
    private ConstructionModelValidator constructionModelValidator;

    @InjectMocks
    private ConstructionService underTest;

    @Mock
    private ConstructionModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(constructionDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.CONSTRUCTION);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(constructionModelValidator).validate(model);
        verify(constructionDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(constructionDao.findById(ID)).willReturn(Optional.of(model));

        Optional<ConstructionModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(constructionDao.getByLocation(ID)).willReturn(Arrays.asList(model));

        List<ConstructionModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(constructionDao).deleteById(ID);
    }
}