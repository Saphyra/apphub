package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LineConverter {
    private final UuidConverter uuidConverter;
    private final CoordinateConverter coordinateConverter;

    public Line convertEntity(LineEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        return Line.builder()
            .a(coordinateConverter.convertEntity(entity.getA()))
            .b(coordinateConverter.convertEntity(entity.getB()))
            .build();
    }

    public LineEntity convertDomain(Line domain, UUID referenceId) {
        if (isNull(domain)) {
            return null;
        }
        String aId = referenceId + "-a";
        String bId = referenceId + "-b";
        return LineEntity.builder()
            .referenceId(uuidConverter.convertDomain(referenceId))
            .aId(aId)
            .bId(bId)
            .a(coordinateConverter.convertDomain(domain.getA(), aId))
            .b(coordinateConverter.convertDomain(domain.getA(), bId))
            .build();
    }
}
