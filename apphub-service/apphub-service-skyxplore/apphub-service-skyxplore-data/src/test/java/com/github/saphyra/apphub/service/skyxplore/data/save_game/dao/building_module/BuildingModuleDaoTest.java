package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BuildingModuleDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_ID_STRING = "building-module-id";
    private static final int PAGE = 342;
    private static final int ITEMS_PER_PAGE = 45;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BuildingModuleConverter converter;

    @Mock
    private BuildingModuleRepository repository;

    @InjectMocks
    private BuildingModuleDao underTest;

    @Mock
    private BuildingModuleEntity entity;

    @Mock
    private BuildingModuleModel domain;

    @Test
    void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        then(repository).should().deleteByGameId(GAME_ID_STRING);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(BUILDING_MODULE_ID)).willReturn(BUILDING_MODULE_ID_STRING);
        given(repository.existsById(BUILDING_MODULE_ID_STRING)).willReturn(true);

        underTest.deleteById(BUILDING_MODULE_ID);

        then(repository).should().deleteById(BUILDING_MODULE_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(domain);
    }
}