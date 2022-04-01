package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AllianceConverterTest {
    private static final String ALLIANCE_ID_STRING = "alliance-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String NAME = "name";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AllianceConverter underTest;

    @Test
    public void convertEntity() {
        AllianceEntity entity = AllianceEntity.builder()
            .allianceId(ALLIANCE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .name(NAME)
            .build();
        given(uuidConverter.convertEntity(ALLIANCE_ID_STRING)).willReturn(ALLIANCE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);

        AllianceModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(ALLIANCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.ALLIANCE);
        assertThat(result.getName()).isEqualTo(NAME);
    }

    @Test
    public void convertDomain() {
        AllianceModel model = new AllianceModel();
        model.setId(ALLIANCE_ID);
        model.setGameId(GAME_ID);
        model.setName(NAME);

        given(uuidConverter.convertDomain(ALLIANCE_ID)).willReturn(ALLIANCE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        AllianceEntity result = underTest.convertDomain(model);

        assertThat(result.getAllianceId()).isEqualTo(ALLIANCE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getName()).isEqualTo(NAME);
    }
}