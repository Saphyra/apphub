package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CoordinateConverter {
    private final UuidConverter uuidConverter;

    public Coordinate convertEntity(CoordinateEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        return new Coordinate(entity.getX(), entity.getY());
    }

    public CoordinateEntity convertDomain(Coordinate domain, UUID id) {
        return convertDomain(domain, uuidConverter.convertDomain(id));
    }

    public CoordinateEntity convertDomain(Coordinate domain, String id) {
        if (isNull(domain)) {
            return null;
        }
        return new CoordinateEntity(id, domain.getX(), domain.getY());
    }
}
