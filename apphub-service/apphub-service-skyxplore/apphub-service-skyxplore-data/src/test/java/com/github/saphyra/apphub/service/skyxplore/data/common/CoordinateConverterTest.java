package com.github.saphyra.apphub.service.skyxplore.data.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Coordinate;

@RunWith(MockitoJUnitRunner.class)
public class CoordinateConverterTest {
    private static final Double X = 2342D;
    private static final Double Y = 2452D;
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CoordinateConverter underTest;

    @Test
    public void convertEntity_null() {
        assertThat(underTest.convertEntity(null)).isNull();
    }

    @Test
    public void convertEntity() {
        CoordinateEntity entity = CoordinateEntity.builder()
            .x(X)
            .y(Y)
            .build();

        Coordinate result = underTest.convertEntity(entity);

        assertThat(result.getX()).isEqualTo(X);
        assertThat(result.getY()).isEqualTo(Y);
    }

    @Test
    public void convertDomain_uuid() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);

        Coordinate coordinate = new Coordinate(X, Y);

        CoordinateEntity result = underTest.convertDomain(coordinate, ID);

        assertThat(result.getReferenceId()).isEqualTo(ID_STRING);
        assertThat(result.getX()).isEqualTo(X);
        assertThat(result.getY()).isEqualTo(Y);
    }

    @Test
    public void convertDomain_null() {
        assertThat(underTest.convertDomain(null, ID_STRING)).isNull();
    }

    @Test
    public void convertDomain() {
        Coordinate coordinate = new Coordinate(X, Y);

        CoordinateEntity result = underTest.convertDomain(coordinate, ID_STRING);

        assertThat(result.getReferenceId()).isEqualTo(ID_STRING);
        assertThat(result.getX()).isEqualTo(X);
        assertThat(result.getY()).isEqualTo(Y);
    }
}