package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
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
public class SkillServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private SkillDao skillDao;

    @Mock
    private SkillModelValidator skillModelValidator;

    @InjectMocks
    private SkillService underTest;

    @Mock
    private SkillModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(skillDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SKILL);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(skillModelValidator).validate(model);
        verify(skillDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(skillDao.findById(ID)).willReturn(Optional.of(model));

        Optional<SkillModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(skillDao.getByCitizenId(ID)).willReturn(Arrays.asList(model));

        List<SkillModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }
}