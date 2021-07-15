package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
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
public class AllianceDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_ID_STRING = "alliance-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AllianceRepository repository;

    @Mock
    private AllianceConverter converter;

    @InjectMocks
    private AllianceDao underTest;

    @Mock
    private AllianceEntity entity;

    @Mock
    private AllianceModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void getByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<AllianceModel> result = underTest.getByGameId(GAME_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ALLIANCE_ID)).willReturn(ALLIANCE_ID_STRING);
        given(repository.findById(ALLIANCE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<AllianceModel> result = underTest.findById(ALLIANCE_ID);

        assertThat(result).contains(model);
    }
}