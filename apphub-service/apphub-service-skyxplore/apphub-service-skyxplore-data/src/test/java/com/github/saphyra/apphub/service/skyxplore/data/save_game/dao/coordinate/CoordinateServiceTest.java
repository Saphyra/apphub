package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class CoordinateServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private CoordinateDao coordinateDao;

    @Mock
    private CoordinateModelValidator coordinateModelValidator;

    @InjectMocks
    private CoordinateService underTest;

    @Mock
    private CoordinateModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(coordinateDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.COORDINATE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(coordinateModelValidator).validate(model);
        verify(coordinateDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(coordinateDao.findById(ID)).willReturn(Optional.of(model));

        Optional<CoordinateModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(coordinateDao.getByReferenceId(ID)).willReturn(Arrays.asList(model));

        List<CoordinateModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(coordinateDao).deleteById(ID);
    }
}