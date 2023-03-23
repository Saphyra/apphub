package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Constructions extends Vector<Construction> {
    public Optional<Construction> findByExternalReference(UUID externalReference) {
        return stream()
            .filter(construction -> construction.getExternalReference().equals(externalReference))
            .findAny();
    }

    public void deleteById(UUID constructionId) {
        removeIf(construction -> construction.getConstructionId().equals(constructionId));
    }
}
