package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class SpaceshipWriteBuffer extends WriteBuffer<ItemDomainId, Spaceship> {
    private final SpaceshipRepository spaceshipRepository;
    private final SpaceshipConverter spaceshipConverter;
    private final ErrorReporterService errorReporterService;

    protected SpaceshipWriteBuffer(DateTimeUtil dateTimeUtil, SpaceshipRepository spaceshipRepository, SpaceshipConverter spaceshipConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.spaceshipRepository = spaceshipRepository;
        this.spaceshipConverter = spaceshipConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Spaceship spaceship) {
        return ItemDomainId.builder()
            .itemName(spaceship.getItemName())
            .externalReference(spaceship.getExternalReference())
            .locationType(spaceship.getLocationType())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Spaceship> bufferCopy) {
        List<SpaceshipEntity> entities = new ArrayList<>(spaceshipConverter.convertDomain(bufferCopy));
        List<String> itemNames = entities.stream().map(SpaceshipEntity::getItemName).distinct().toList();
        List<String> stationIds = entities.stream().map(SpaceshipEntity::getStationId).filter(id -> id != null).distinct().toList();
        List<String> fleetCarrierIds = entities.stream().map(SpaceshipEntity::getFleetCarrierId).filter(id -> id != null).distinct().toList();

        Map<BiWrapper<String, String>, SpaceshipEntity> existingStations = stationIds.isEmpty()
            ? Map.of()
            : spaceshipRepository.getByStationIdInAndItemNameIn(stationIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getStationId(), entity.getItemName()), Function.identity()));
        Map<BiWrapper<String, String>, SpaceshipEntity> existingFleetCarriers = fleetCarrierIds.isEmpty()
            ? Map.of()
            : spaceshipRepository.getByFleetCarrierIdInAndItemNameIn(fleetCarrierIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getFleetCarrierId(), entity.getItemName()), Function.identity()));

        entities.forEach(entity -> {
            BiWrapper<String, String> key = new BiWrapper<>(
                entity.getStationId() == null ? entity.getFleetCarrierId() : entity.getStationId(),
                entity.getItemName()
            );
            SpaceshipEntity existing = entity.getStationId() == null
                ? existingFleetCarriers.get(key)
                : existingStations.get(key);
            if (existing != null) {
                entity.setId(existing.getId());
            }
        });

        try {
            spaceshipRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving Spaceship batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    spaceshipRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving Spaceship", e2);
                }
            });
        }
    }
}
