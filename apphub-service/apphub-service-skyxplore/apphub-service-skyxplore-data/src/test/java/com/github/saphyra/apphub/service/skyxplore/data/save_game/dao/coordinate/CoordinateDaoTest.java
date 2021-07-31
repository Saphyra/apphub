package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class CoordinateDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID COORDINATE_ID = UUID.randomUUID();
    private static final String COORDINATE_ID_STRING = "coordinate-id";
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final String REFERENCE_ID_STRING = "reference-id";

    @Mock
    private CoordinateRepository repository;

    @Mock
    private CoordinateConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CoordinateDao underTest;

    @Mock
    private CoordinateEntity entity;

    @Mock
    private CoordinateModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(COORDINATE_ID)).willReturn(COORDINATE_ID_STRING);
        given(repository.findById(COORDINATE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<CoordinateModel> result = underTest.findById(COORDINATE_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByReferenceId() {
        given(uuidConverter.convertDomain(REFERENCE_ID)).willReturn(REFERENCE_ID_STRING);
        given(repository.getByReferenceId(REFERENCE_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<CoordinateModel> result = underTest.getByReferenceId(REFERENCE_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(COORDINATE_ID)).willReturn(COORDINATE_ID_STRING);
        given(repository.existsById(COORDINATE_ID_STRING)).willReturn(true);

        underTest.deleteById(COORDINATE_ID);

        verify(repository).deleteById(COORDINATE_ID_STRING);
    }
}