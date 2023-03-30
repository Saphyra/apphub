package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class Constructions extends Vector<Construction> {
    public Construction findByExternalReferenceValidated(UUID externalReference) {
        return findByExternalReference(externalReference)
            .orElseThrow();
    }

    public Optional<Construction> findByExternalReference(UUID externalReference) {
        return stream()
            .filter(construction -> construction.getExternalReference().equals(externalReference))
            .findAny();
    }

    public void deleteById(UUID constructionId) {
        removeIf(construction -> construction.getConstructionId().equals(constructionId));
    }

    public List<Construction> getByLocationAndType(UUID location, ConstructionType type) {
        return stream()
            .filter(construction -> construction.getLocation().equals(location))
            .filter(construction -> construction.getConstructionType() == type)
            .collect(Collectors.toList());
    }

    public Construction findByIdValidated(UUID constructionId) {
        return stream()
            .filter(construction -> construction.getConstructionId().equals(constructionId))
            .findAny()
            .orElseThrow();
    }
}
