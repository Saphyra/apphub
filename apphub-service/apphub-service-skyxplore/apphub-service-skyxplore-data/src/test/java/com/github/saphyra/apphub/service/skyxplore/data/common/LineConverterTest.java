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
import com.github.saphyra.apphub.lib.geometry.Line;

@RunWith(MockitoJUnitRunner.class)
public class LineConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CoordinateConverter coordinateConverter;

    @InjectMocks
    private LineConverter underTest;

    @Mock
    private CoordinateEntity aEntity;

    @Mock
    private CoordinateEntity bEntity;

    @Mock
    private Coordinate a;

    @Mock
    private Coordinate b;

    @Test
    public void convertEntity_null() {
        assertThat(underTest.convertEntity(null)).isNull();
    }

    @Test
    public void convertEntity() {
        LineEntity entity = LineEntity.builder()
            .a(aEntity)
            .b(bEntity)
            .build();

        given(coordinateConverter.convertEntity(aEntity)).willReturn(a);
        given(coordinateConverter.convertEntity(bEntity)).willReturn(b);

        Line line = underTest.convertEntity(entity);

        assertThat(line.getA()).isEqualTo(a);
        assertThat(line.getB()).isEqualTo(b);
    }

    @Test
    public void convertDomain_null() {
        assertThat(underTest.convertDomain(null, ID)).isNull();
    }

    @Test
    public void convertDomain() {
        Line line = new Line(a, b);

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(coordinateConverter.convertDomain(a, ID_STRING + "-a")).willReturn(aEntity);
        given(coordinateConverter.convertDomain(b, ID_STRING + "-b")).willReturn(bEntity);

        LineEntity result = underTest.convertDomain(line, ID);

        assertThat(result.getReferenceId()).isEqualTo(ID_STRING);
        assertThat(result.getA()).isEqualTo(aEntity);
        assertThat(result.getB()).isEqualTo(bEntity);
        assertThat(result.getAId()).isEqualTo(ID_STRING + "-a");
        assertThat(result.getBId()).isEqualTo(ID_STRING + "-b");
    }
}