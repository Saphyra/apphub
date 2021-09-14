package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AiCountCalculatorTest {
    private static final double PLAYER_COUNT = 2453d;
    private static final Double MIN_AI_SPAWN_CHANCE = 3124d;
    private static final Double MAX_AI_SPAWN_CHANCE = 65234d;
    @Mock
    private GameCreationProperties gameCreationProperties;

    @Mock
    private Random random;

    @InjectMocks
    private AiCountCalculator underTest;

    @Mock
    private GameCreationProperties.PlayerCreationProperties playerCreationProperties;

    @Before
    public void setUp() {
        given(gameCreationProperties.getPlayer()).willReturn(playerCreationProperties);
        given(playerCreationProperties.getAiSpawnChance()).willReturn(CollectionUtils.singleValueMap(AiPresence.COMMON, new Range<>(MIN_AI_SPAWN_CHANCE, MAX_AI_SPAWN_CHANCE)));
    }

    @Test
    public void oneAlliance_noAisToGenerate() {
        given(random.randDouble(PLAYER_COUNT * MIN_AI_SPAWN_CHANCE, PLAYER_COUNT * MAX_AI_SPAWN_CHANCE)).willReturn(0d);

        int result = underTest.getAiCount(PLAYER_COUNT, AiPresence.COMMON, 1);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void oneAlliance_hasAisToGenerate() {
        given(random.randDouble(PLAYER_COUNT * MIN_AI_SPAWN_CHANCE, PLAYER_COUNT * MAX_AI_SPAWN_CHANCE)).willReturn(2d);

        int result = underTest.getAiCount(PLAYER_COUNT, AiPresence.COMMON, 1);

        assertThat(result).isEqualTo(2);
    }

    @Test
    public void multipleAlliances() {
        given(random.randDouble(PLAYER_COUNT * MIN_AI_SPAWN_CHANCE, PLAYER_COUNT * MAX_AI_SPAWN_CHANCE)).willReturn(0d);

        int result = underTest.getAiCount(PLAYER_COUNT, AiPresence.COMMON, 2);

        assertThat(result).isEqualTo(0);
    }
}