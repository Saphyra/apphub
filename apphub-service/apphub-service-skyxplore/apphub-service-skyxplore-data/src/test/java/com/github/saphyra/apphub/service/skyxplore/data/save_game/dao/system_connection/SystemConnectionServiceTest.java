package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
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
public class SystemConnectionServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private SystemConnectionDao systemConnectionDao;

    @Mock
    private SystemConnectionModelValidator systemConnectionModelValidator;

    @InjectMocks
    private SystemConnectionService underTest;

    @Mock
    private SystemConnectionModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(systemConnectionDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SYSTEM_CONNECTION);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(systemConnectionModelValidator).validate(model);
        verify(systemConnectionDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(systemConnectionDao.findById(ID)).willReturn(Optional.of(model));

        Optional<SystemConnectionModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(systemConnectionDao.getByGameId(ID)).willReturn(Arrays.asList(model));

        List<SystemConnectionModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(systemConnectionDao).deleteById(ID);
    }
}