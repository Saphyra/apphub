package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item;

import java.util.UUID;

public interface Item {
    String getItemName();

    UUID getExternalReference();

    ItemLocationType getLocationType();

    default UUID getStationId(){
        return getLocationType() == ItemLocationType.STATION ? getExternalReference() : null;
    }

    default UUID getFleetCarrierId(){
        return getLocationType() == ItemLocationType.FLEET_CARRIER ? getExternalReference() : null;
    }
}
