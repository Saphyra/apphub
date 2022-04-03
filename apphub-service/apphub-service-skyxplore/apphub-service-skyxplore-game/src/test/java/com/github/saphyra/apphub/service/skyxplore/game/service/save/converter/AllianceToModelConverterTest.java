package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AllianceToModelConverterTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private AllianceToModelConverter underTest;

    @Mock
    private Game game;

    @Test
    public void convert() {
        Alliance alliance = Alliance.builder()
            .allianceId(ALLIANCE_ID)
            .allianceName(ALLIANCE_NAME)
            .build();
        given(game.getGameId()).willReturn(GAME_ID);

        AllianceModel result = underTest.convert(alliance, game);

        assertThat(result.getId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.ALLIANCE);
        assertThat(result.getName()).isEqualTo(ALLIANCE_NAME);
    }
}