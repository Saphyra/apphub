package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
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
public class CitizenDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String CITIZEN_ID_STRING = "citizen-id";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";
    private static final Integer PAGE = 34;
    private static final Integer ITEMS_PER_PAGE = 43;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CitizenConverter converter;

    @Mock
    private CitizenRepository repository;

    @InjectMocks
    private CitizenDao underTest;

    @Mock
    private CitizenEntity entity;

    @Mock
    private CitizenModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);
        given(repository.findById(CITIZEN_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<CitizenModel> result = underTest.findById(CITIZEN_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByLocation() {
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(repository.getByLocation(LOCATION_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<CitizenModel> result = underTest.getByLocation(LOCATION);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);
        given(repository.existsById(CITIZEN_ID_STRING)).willReturn(true);

        underTest.deleteById(CITIZEN_ID);

        verify(repository).deleteById(CITIZEN_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        List<CitizenModel> result = underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE);

        assertThat(result).containsExactly(model);
    }
}