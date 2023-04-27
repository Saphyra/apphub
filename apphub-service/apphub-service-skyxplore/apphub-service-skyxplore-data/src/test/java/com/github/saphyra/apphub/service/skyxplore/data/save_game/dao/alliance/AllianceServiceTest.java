package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AllianceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private AllianceDao dao;

    @Mock
    private AllianceModelValidator allianceModelValidator;

    @InjectMocks
    private AllianceService underTest;

    @Mock
    private AllianceModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(dao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.ALLIANCE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(allianceModelValidator).validate(model);
        verify(dao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(dao).deleteById(ID);
    }

    @Test
    void loadPage() {
        given(dao.getByGameId(GAME_ID)).willReturn(List.of(model));

        assertThat(underTest.loadPage(GAME_ID, 0, 0)).containsExactly(model);
    }
}