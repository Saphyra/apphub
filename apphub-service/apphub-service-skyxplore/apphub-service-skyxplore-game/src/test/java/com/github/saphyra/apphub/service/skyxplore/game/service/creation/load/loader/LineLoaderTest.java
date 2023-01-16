package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineLoaderTest {
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final UUID A_ID = UUID.randomUUID();
    private static final UUID B_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private CoordinateLoader coordinateLoader;

    @InjectMocks
    private LineLoader underTest;

    @Mock
    private LineModel lineModel;

    @Mock
    private CoordinateModel coordinateModel1;

    @Mock
    private CoordinateModel coordinateModel2;

    @Mock
    private Coordinate coordinate1;

    @Mock
    private Coordinate coordinate2;

    @Test
    public void loadOne_notFound() {
        assertThat(underTest.loadOne(REFERENCE_ID)).isNull();
    }

    @Test
    public void loadOne() {
        given(gameItemLoader.loadChildren(REFERENCE_ID, GameItemType.LINE, LineModel[].class)).willReturn(Arrays.asList(lineModel));
        given(lineModel.getA()).willReturn(A_ID);
        given(lineModel.getB()).willReturn(B_ID);
        given(coordinateLoader.loadById(A_ID)).willReturn(coordinateModel1);
        given(coordinateLoader.loadById(B_ID)).willReturn(coordinateModel2);
        given(coordinateModel1.getCoordinate()).willReturn(coordinate1);
        given(coordinateModel2.getCoordinate()).willReturn(coordinate2);

        LineModelWrapper result = underTest.loadOne(REFERENCE_ID);

        assertThat(result.getModel()).isEqualTo(lineModel);
        assertThat(result.getLine()).isEqualTo(new Line(coordinate1, coordinate2));
        assertThat(result.getA()).isEqualTo(coordinateModel1);
        assertThat(result.getB()).isEqualTo(coordinateModel2);
    }
}