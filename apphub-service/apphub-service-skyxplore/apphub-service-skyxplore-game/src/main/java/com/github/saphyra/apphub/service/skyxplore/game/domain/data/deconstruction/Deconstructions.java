package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class Deconstructions extends Vector<Deconstruction> {
    public Optional<Deconstruction> findByExternalReference(UUID externalReference) {
        return stream()
            .filter(deconstruction -> deconstruction.getExternalReference().equals(externalReference))
            .findAny();
    }
}
