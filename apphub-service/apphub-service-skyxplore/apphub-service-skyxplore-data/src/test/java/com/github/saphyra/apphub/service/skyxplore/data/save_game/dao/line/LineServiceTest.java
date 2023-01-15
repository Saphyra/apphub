package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
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
public class LineServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private LineDao lineDao;

    @Mock
    private LineModelValidator lineModelValidator;

    @InjectMocks
    private LineService underTest;

    @Mock
    private LineModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(lineDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.LINE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(lineModelValidator).validate(model);
        verify(lineDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(lineDao.findById(ID)).willReturn(Optional.of(model));

        Optional<LineModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(lineDao.getByReferenceId(ID)).willReturn(Arrays.asList(model));

        List<LineModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(lineDao).deleteById(ID);
    }
}