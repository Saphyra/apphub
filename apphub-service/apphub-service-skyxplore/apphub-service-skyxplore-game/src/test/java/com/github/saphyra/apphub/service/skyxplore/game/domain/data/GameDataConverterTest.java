package com.github.saphyra.apphub.service.skyxplore.game.domain.data;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameDataConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDataToModelConverter gameDataToModelConverter;

    private GameDataConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private GameItem gameItem;

    @BeforeEach
    void setUp() {
        underTest = new GameDataConverter(List.of(gameDataToModelConverter));
    }

    @Test
    void convert() {
        List gameItems = List.of(gameItem);
        given(gameDataToModelConverter.convert(GAME_ID, gameData)).willReturn(gameItems);

        assertThat(underTest.convert(GAME_ID, gameData)).containsExactly(gameItem);
    }
}