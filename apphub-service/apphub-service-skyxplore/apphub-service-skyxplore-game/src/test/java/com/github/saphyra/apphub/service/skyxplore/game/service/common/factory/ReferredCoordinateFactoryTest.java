package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReferredCoordinateFactoryTest {
    private static final UUID REFERRED_COORDINATE_ID = UUID.randomUUID();
    private static final UUID REFERENCE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ReferredCoordinateFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(REFERRED_COORDINATE_ID);

        ReferredCoordinate result = underTest.create(REFERENCE_ID, coordinate);

        assertThat(result.getReferredCoordinateId()).isEqualTo(REFERRED_COORDINATE_ID);
        assertThat(result.getReferenceId()).isEqualTo(REFERENCE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
    }
}