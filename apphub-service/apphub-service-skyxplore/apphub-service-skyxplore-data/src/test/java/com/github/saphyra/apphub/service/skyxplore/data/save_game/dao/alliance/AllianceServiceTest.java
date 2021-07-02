package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllianceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AllianceDao allianceDao;

    @Mock
    private AllianceModelValidator allianceModelValidator;

    @InjectMocks
    private AllianceService underTest;

    @Mock
    private AllianceModel allianceModel;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(allianceDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.ALLIANCE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(allianceModel));

        verify(allianceModelValidator).validate(allianceModel);
        verify(allianceDao).saveAll(Arrays.asList(allianceModel));
    }
}