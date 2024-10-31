package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class ConstructionAreas extends Vector<ConstructionArea> {
    public Optional<ConstructionArea> findBySurfaceId(UUID surfaceId) {
        return stream()
            .filter(constructionArea -> constructionArea.getSurfaceId().equals(surfaceId))
            .findAny();
    }

    public ConstructionArea findByConstructionAreaIdValidated(UUID constructionAreaId) {
        return stream()
            .filter(constructionArea -> constructionArea.getConstructionAreaId().equals(constructionAreaId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ConstructionArea not found by id " + constructionAreaId));
    }
}
