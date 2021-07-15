package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
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
public class BuildingServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private BuildingDao buildingDao;

    @Mock
    private BuildingModelValidator buildingModelValidator;

    @InjectMocks
    private BuildingService underTest;

    @Mock
    private BuildingModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(buildingDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.BUILDING);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(buildingModelValidator).validate(model);
        buildingDao.saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(buildingDao.findById(ID)).willReturn(Optional.of(model));

        Optional<BuildingModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent_notFound() {
        given(buildingDao.findBySurfaceId(ID)).willReturn(Optional.empty());

        List<BuildingModel> result = underTest.getByParent(ID);

        assertThat(result).isEmpty();
    }

    @Test
    public void getByParent() {
        given(buildingDao.findBySurfaceId(ID)).willReturn(Optional.of(model));

        List<BuildingModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }
}