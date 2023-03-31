package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
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
class CitizenAllocationDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final String CITIZEN_ALLOCATION_ID_STRING = "citizen-allocation-id";
    private static final int ITEMS_PER_PAGE = 245;
    private static final int PAGE = 36;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CitizenAllocationConverter converter;

    @Mock
    private CitizenAllocationRepository repository;

    @InjectMocks
    private CitizenAllocationDao underTest;

    @Mock
    private CitizenAllocationModel model;

    @Mock
    private CitizenAllocationEntity entity;

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

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}