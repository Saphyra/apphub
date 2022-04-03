package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
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
public class LineConverterTest {
    private static final UUID LINE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID A = UUID.randomUUID();
    private static final UUID B = UUID.randomUUID();
    private static final String LINE_ID_STRING = "line-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String REFERENCE_ID_STRING = "reference-id";
    private static final String A_STRING = "a";
    private static final String B_STRING = "b";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LineConverter underTest;

    @Test
    public void convertEntity() {
        LineEntity entity = LineEntity.builder()
            .lineId(LINE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .referenceId(REFERENCE_ID_STRING)
            .a(A_STRING)
            .b(B_STRING)
            .build();

        given(uuidConverter.convertEntity(LINE_ID_STRING)).willReturn(LINE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(REFERENCE_ID_STRING)).willReturn(REFERENCE_ID);
        given(uuidConverter.convertEntity(A_STRING)).willReturn(A);
        given(uuidConverter.convertEntity(B_STRING)).willReturn(B);

        LineModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(LINE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.LINE);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getA()).isEqualTo(A);
        assertThat(result.getB()).isEqualTo(B);
    }

    @Test
    public void convertDomain() {
        LineModel model = new LineModel();
        model.setId(LINE_ID);
        model.setGameId(GAME_ID);
        model.setReferenceId(REFERENCE_ID);
        model.setA(A);
        model.setB(B);

        given(uuidConverter.convertDomain(LINE_ID)).willReturn(LINE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(REFERENCE_ID)).willReturn(REFERENCE_ID_STRING);
        given(uuidConverter.convertDomain(A)).willReturn(A_STRING);
        given(uuidConverter.convertDomain(B)).willReturn(B_STRING);

        LineEntity result = underTest.convertDomain(model);

        assertThat(result.getLineId()).isEqualTo(LINE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID_STRING);
        assertThat(result.getA()).isEqualTo(A_STRING);
        assertThat(result.getB()).isEqualTo(B_STRING);
    }
}