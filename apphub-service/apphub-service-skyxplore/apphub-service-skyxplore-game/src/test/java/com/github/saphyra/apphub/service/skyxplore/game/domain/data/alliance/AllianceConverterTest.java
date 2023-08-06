package com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AllianceConverterTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private AllianceConverter underTest;

    @Mock
    private Game game;

    @Test
    public void toModel() {
        Alliance alliance = Alliance.builder()
            .allianceId(ALLIANCE_ID)
            .allianceName(ALLIANCE_NAME)
            .build();
        given(game.getGameId()).willReturn(GAME_ID);

        AllianceModel result = underTest.toModel(alliance, game);

        assertThat(result.getId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.ALLIANCE);
        assertThat(result.getName()).isEqualTo(ALLIANCE_NAME);
    }
}