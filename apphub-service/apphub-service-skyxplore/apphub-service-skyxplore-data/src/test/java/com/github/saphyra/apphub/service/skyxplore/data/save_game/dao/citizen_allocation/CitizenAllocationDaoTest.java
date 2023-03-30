package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

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
class CitizenAllocationDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final String CITIZEN_ALLOCATION_ID_STRING = "citizen-allocation-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CitizenAllocationConverter converter;

    @Mock
    private CitizenAllocationRepository repository;

    @InjectMocks
    private CitizenAllocationDao underTest;

    @Test
    void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(CITIZEN_ALLOCATION_ID)).willReturn(CITIZEN_ALLOCATION_ID_STRING);
        given(repository.existsById(CITIZEN_ALLOCATION_ID_STRING)).willReturn(true);

        underTest.deleteById(CITIZEN_ALLOCATION_ID);

        verify(repository).deleteById(CITIZEN_ALLOCATION_ID_STRING);
    }
}