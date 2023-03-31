package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuildingAllocationDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final String BUILDING_ALLOCATION_ID_STRING = "building-allocation-id";
    private static final int PAGE = 32;
    private static final int ITEMS_PER_PAGE = 364;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BuildingAllocationConverter converter;

    @Mock
    private BuildingAllocationRepository repository;

    @InjectMocks
    private BuildingAllocationDao underTest;

    @Mock
    private BuildingAllocationEntity entity;

    @Mock
    private BuildingAllocationModel model;

    @Test
    void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(BUILDING_ALLOCATION_ID)).willReturn(BUILDING_ALLOCATION_ID_STRING);
        given(repository.existsById(BUILDING_ALLOCATION_ID_STRING)).willReturn(true);

        underTest.deleteById(BUILDING_ALLOCATION_ID);

        verify(repository).deleteById(BUILDING_ALLOCATION_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}