package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Vector;

@NoArgsConstructor
public class ReservedStorages extends Vector<ReservedStorage> {
    public ReservedStorages(Collection<ReservedStorage> reservedStorages) {
        addAll(reservedStorages);
    }
}
