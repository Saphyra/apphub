package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
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
public class AllianceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private AllianceDao allianceDao;

    @Mock
    private AllianceModelValidator allianceModelValidator;

    @InjectMocks
    private AllianceService underTest;

    @Mock
    private AllianceModel model;

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
        underTest.save(Arrays.asList(model));

        verify(allianceModelValidator).validate(model);
        verify(allianceDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(allianceDao.findById(ID)).willReturn(Optional.of(model));

        Optional<AllianceModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(allianceDao.getByGameId(ID)).willReturn(Arrays.asList(model));

        List<AllianceModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(allianceDao).deleteById(ID);
    }
}