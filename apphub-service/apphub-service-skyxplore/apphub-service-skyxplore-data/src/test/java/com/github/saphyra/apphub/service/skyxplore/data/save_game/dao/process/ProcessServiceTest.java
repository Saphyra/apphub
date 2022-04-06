package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
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
public class ProcessServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private ProcessDao processDao;

    @Mock
    private ProcessModelValidator processModelValidator;

    @InjectMocks
    private ProcessService underTest;

    @Mock
    private ProcessModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(processDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PROCESS);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(processModelValidator).validate(model);
        verify(processDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(processDao.findById(PROCESS_ID)).willReturn(Optional.of(model));

        Optional<ProcessModel> result = underTest.findById(PROCESS_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(processDao.getByGameId(GAME_ID)).willReturn(Arrays.asList(model));

        List<ProcessModel> result = underTest.getByParent(GAME_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(PROCESS_ID);

        verify(processDao).deleteById(PROCESS_ID);
    }
}