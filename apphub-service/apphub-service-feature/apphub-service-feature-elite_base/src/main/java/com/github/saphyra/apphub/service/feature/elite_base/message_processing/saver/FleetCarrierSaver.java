package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier.FleetCarrierFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.google.common.util.concurrent.Striped;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class FleetCarrierSaver {
    private static final Striped<Lock> CARRIER_ID_LOCK = Striped.lock(8);

    private final FleetCarrierDao fleetCarrierDao;
    private final FleetCarrierFactory fleetCarrierFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public FleetCarrier save(LocalDateTime timestamp, String carrierId, String carrierName, Long marketId) {
        return save(timestamp, null, carrierId, carrierName, null, marketId);
    }

    public FleetCarrier save(LocalDateTime timestamp, String carrierId, Long marketId) {
        return save(timestamp, null, carrierId, null, null, marketId);
    }

    public FleetCarrier save(LocalDateTime timestamp, UUID starSystemId, String carrierId, String carrierName, FleetCarrierDockingAccess dockingAccess, Long marketId) {
        if (isBlank(carrierId)) {
            throw new IllegalArgumentException("CarrierId must not be blank");
        }

        Lock carrierIdLock = CARRIER_ID_LOCK.get(carrierId);
        lock(carrierIdLock);

        try {
            log.debug("Saving FleetCarrier {}", carrierId);

            Optional<FleetCarrier> maybeCarrier = fleetCarrierDao.findByCarrierId(carrierId);
            if (maybeCarrier.isPresent()) {
                FleetCarrier fleetCarrier = maybeCarrier.get();

                updateFields(timestamp, fleetCarrier, starSystemId, carrierName, dockingAccess, marketId, carrierId);

                return fleetCarrier;
            } else {
                if(isNull(starSystemId)){
                    throw new MessageProcessingDelayedException("StarSystemId of FleetCarrier " + carrierId + " is null");
                }

                FleetCarrier created = fleetCarrierFactory.create(carrierId, carrierName, starSystemId, dockingAccess, marketId);
                log.debug("Saving new {}", created);
                fleetCarrierDao.clearMarketId(created.getId(), marketId);
                fleetCarrierDao.save(created);
                saveLastUpdate(timestamp, created);
                return created;
            }
        } finally {
            carrierIdLock.unlock();
        }
    }

    @SneakyThrows
    private void lock(Lock lock) {
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }
    }

    private void updateFields(LocalDateTime timestamp, FleetCarrier carrier, UUID starSystemId, String carrierName, FleetCarrierDockingAccess dockingAccess, Long marketId, String carrierId) {
        LocalDateTime lastUpdated = lastUpdateDao.findByIdOrDefault(carrier.getId(), ObjectType.FLEET_CARRIER).getLastUpdate();
        if (timestamp.isBefore(lastUpdated)) {
            log.debug("StarSystem {} has newer data than {}", carrier.getId(), timestamp);
            return;
        }

        fleetCarrierDao.clearMarketId(carrier.getId(), marketId);
        saveLastUpdate(timestamp, carrier);

        List.of(
                new UpdateHelper(carrierId, carrier::getCarrierId, () -> carrier.setCarrierId(carrierId)),
                new UpdateHelper(starSystemId, carrier::getStarSystemId, () -> carrier.setStarSystemId(starSystemId)),
                new UpdateHelper(carrierName, carrier::getCarrierName, () -> carrier.setCarrierName(carrierName)),
                new UpdateHelper(dockingAccess, carrier::getDockingAccess, () -> carrier.setDockingAccess(dockingAccess)),
                new UpdateHelper(marketId, carrier::getMarketId, () -> carrier.setMarketId(marketId))
            )
            .forEach(UpdateHelper::modify);

        fleetCarrierDao.save(carrier);
    }

    private void saveLastUpdate(LocalDateTime timestamp, FleetCarrier fleetCarrier) {
        lastUpdateDao.save(lastUpdateFactory.create(fleetCarrier.getId(), ObjectType.FLEET_CARRIER, timestamp));
    }
}
