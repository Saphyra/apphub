package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
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
public class SystemConnectionDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();
    private static final String SYSTEM_CONNECTION_ID_STRING = "system-connection-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SystemConnectionConverter converter;

    @Mock
    private SystemConnectionRepository repository;

    @InjectMocks
    private SystemConnectionDao underTest;

    @Mock
    private SystemConnectionEntity entity;

    @Mock
    private SystemConnectionModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(SYSTEM_CONNECTION_ID)).willReturn(SYSTEM_CONNECTION_ID_STRING);
        given(repository.findById(SYSTEM_CONNECTION_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<SystemConnectionModel> result = underTest.findById(SYSTEM_CONNECTION_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<SystemConnectionModel> result = underTest.getByGameId(GAME_ID);

        assertThat(result).containsExactly(model);
    }
}