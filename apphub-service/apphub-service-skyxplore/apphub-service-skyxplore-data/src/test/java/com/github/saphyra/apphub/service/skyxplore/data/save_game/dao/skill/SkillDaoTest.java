package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final String SKILL_ID_STRING = "skill-id";
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String CITIZEN_ID_STRING = "citizen-id";
    private static final int PAGE = 3254;
    private static final int ITEMS_PER_PAGE = 356;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SkillConverter converter;

    @Mock
    private SkillRepository repository;

    @InjectMocks
    private SkillDao underTest;

    @Mock
    private SkillEntity entity;

    @Mock
    private SkillModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(SKILL_ID)).willReturn(SKILL_ID_STRING);
        given(repository.findById(SKILL_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<SkillModel> result = underTest.findById(SKILL_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByCitizenId() {
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);
        given(repository.getByCitizenId(CITIZEN_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<SkillModel> result = underTest.getByCitizenId(CITIZEN_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(SKILL_ID)).willReturn(SKILL_ID_STRING);
        given(repository.existsById(SKILL_ID_STRING)).willReturn(true);

        underTest.deleteById(SKILL_ID);

        verify(repository).deleteById(SKILL_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}