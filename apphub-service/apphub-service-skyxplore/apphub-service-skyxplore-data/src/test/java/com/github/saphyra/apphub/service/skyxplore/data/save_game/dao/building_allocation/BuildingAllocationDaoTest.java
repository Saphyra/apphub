package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuildingAllocationDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final String BUILDING_ALLOCATION_ID_STRING = "building-allocation-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BuildingAllocationConverter converter;

    @Mock
    private BuildingAllocationRepository repository;

    @InjectMocks
    private BuildingAllocationDao underTest;

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
}