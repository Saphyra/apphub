package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UniverseDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private UniverseConverter converter;

    @Mock
    private UniverseRepository repository;

    @InjectMocks
    private UniverseDao underTest;

    @Mock
    private UniverseEntity entity;

    @Mock
    private UniverseModel model;

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.existsById(GAME_ID_STRING)).willReturn(true);

        underTest.deleteById(GAME_ID);

        verify(repository).deleteById(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.findById(GAME_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<UniverseModel> result = underTest.findById(GAME_ID);

        assertThat(result).contains(model);
    }
}