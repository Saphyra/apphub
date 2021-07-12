package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LineModelWrapperFactoryTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID LINE_ID = UUID.randomUUID();
    private static final UUID A = UUID.randomUUID();
    private static final UUID B = UUID.randomUUID();

    @Mock
    private CoordinateModelFactory coordinateModelFactory;

    @Mock
    private LineModelFactory lineModelFactory;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private LineModelWrapperFactory underTest;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Mock
    private CoordinateModel coordinateModel1;

    @Mock
    private CoordinateModel coordinateModel2;

    @Mock
    private LineModel lineModel;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(LINE_ID);
        given(coordinateModelFactory.create(coordinate1, GAME_ID, LINE_ID)).willReturn(coordinateModel1);
        given(coordinateModelFactory.create(coordinate2, GAME_ID, LINE_ID)).willReturn(coordinateModel2);

        given(coordinateModel1.getId()).willReturn(A);
        given(coordinateModel2.getId()).willReturn(B);
        given(lineModelFactory.create(LINE_ID, GAME_ID, REFERENCE_ID, A, B)).willReturn(lineModel);

        Line line = new Line(coordinate1, coordinate2);

        LineModelWrapper result = underTest.create(line, GAME_ID, REFERENCE_ID);

        assertThat(result.getLine()).isEqualTo(line);
        assertThat(result.getModel()).isEqualTo(lineModel);
        assertThat(result.getA()).isEqualTo(coordinateModel1);
        assertThat(result.getB()).isEqualTo(coordinateModel2);
    }
}