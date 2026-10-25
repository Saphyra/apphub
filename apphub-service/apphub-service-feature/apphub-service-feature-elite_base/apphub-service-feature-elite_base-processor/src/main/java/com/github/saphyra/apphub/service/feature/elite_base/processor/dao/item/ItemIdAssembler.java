package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemIdAssembler {
    private final UuidConverter uuidConverter;

    public String assembleId(Item item) {
        String externalReference = uuidConverter.convertDomain(item.getExternalReference());
        String stationId = "";
        String fleetCarrierId = "";

        if (item.getLocationType() == ItemLocationType.STATION) {
            stationId = externalReference;
        } else if (item.getLocationType() == ItemLocationType.FLEET_CARRIER) {
            fleetCarrierId = externalReference;
        }

        return String.join(":", item.getItemName(), stationId, fleetCarrierId);
    }
}
