package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class FleetCarrierSaver {
    private final FleetCarrierDao fleetCarrierDao;
    private final FleetCarrierFactory fleetCarrierFactory;

    public FleetCarrier save(LocalDateTime timestamp, String carrierId, String carrierName, Long marketId) {
        return save(timestamp, null, carrierId, carrierName, null, marketId);
    }

    public FleetCarrier save(LocalDateTime timestamp, String carrierId, Long marketId) {
        return save(timestamp, null, carrierId, null, null, marketId);
    }

    public synchronized FleetCarrier save(LocalDateTime timestamp, UUID starSystemId, String carrierId, String carrierName, FleetCarrierDockingAccess dockingAccess, Long marketId) {
        if (isBlank(carrierId)) {
            throw new IllegalArgumentException("CarrierId must not be blank");
        }

        log.debug("Saving FleetCarrier {}", carrierId);

        FleetCarrier carrier = fleetCarrierDao.findByCarrierId(carrierId)
            .orElseGet(() -> {
                FleetCarrier created = fleetCarrierFactory.create(carrierId, timestamp, carrierName, starSystemId, dockingAccess, marketId);
                log.debug("Saving new {}", created);
                fleetCarrierDao.save(created);
                return created;
            });

        updateFields(timestamp, carrier, starSystemId, carrierName, dockingAccess, marketId, carrierId);

        log.debug("Saved FleetCarrier {}", carrierId);

        return carrier;
    }

    private void updateFields(LocalDateTime timestamp, FleetCarrier carrier, UUID starSystemId, String carrierName, FleetCarrierDockingAccess dockingAccess, Long marketId, String carrierId) {
        if (timestamp.isBefore(carrier.getLastUpdate())) {
            log.debug("FleetCarrier {} has newer data than {}", carrier.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(carrierId, carrier::getCarrierId, () -> carrier.setCarrierId(carrierId)),
                new UpdateHelper(timestamp, carrier::getLastUpdate, () -> carrier.setLastUpdate(timestamp)),
                new UpdateHelper(starSystemId, carrier::getStarSystemId, () -> carrier.setStarSystemId(starSystemId)),
                new UpdateHelper(carrierName, carrier::getCarrierName, () -> carrier.setCarrierName(carrierName)),
                new UpdateHelper(dockingAccess, carrier::getDockingAccess, () -> carrier.setDockingAccess(dockingAccess)),
                new UpdateHelper(marketId, carrier::getMarketId, () -> carrier.setMarketId(marketId))
            )
            .forEach(UpdateHelper::modify);

        fleetCarrierDao.save(carrier);
    }
}
