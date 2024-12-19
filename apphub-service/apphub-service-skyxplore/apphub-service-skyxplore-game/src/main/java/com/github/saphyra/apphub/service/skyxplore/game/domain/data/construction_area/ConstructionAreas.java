package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.annotations.VisibleForTesting;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@NoArgsConstructor
public class ConstructionAreas extends Vector<ConstructionArea> {
    @VisibleForTesting
    public ConstructionAreas(ConstructionArea... areas) {
        super(Arrays.asList(areas));
    }

    public Optional<ConstructionArea> findBySurfaceId(UUID surfaceId) {
        return stream()
            .filter(constructionArea -> constructionArea.getSurfaceId().equals(surfaceId))
            .findAny();
    }

    public ConstructionArea findByConstructionAreaIdValidated(UUID constructionAreaId) {
        return findByConstructionAreaId(constructionAreaId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ConstructionArea not found by id " + constructionAreaId));
    }

    public Optional<ConstructionArea> findByConstructionAreaId(UUID constructionAreaId) {
        return stream()
            .filter(constructionArea -> constructionArea.getConstructionAreaId().equals(constructionAreaId))
            .findAny();
    }
}
