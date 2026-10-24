package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

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
class EquipmentWriteBuffer extends WriteBuffer<ItemDomainId, Equipment> {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentConverter equipmentConverter;
    private final ErrorReporterService errorReporterService;

    EquipmentWriteBuffer(DateTimeUtil dateTimeUtil, EquipmentRepository equipmentRepository, EquipmentConverter equipmentConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.equipmentRepository = equipmentRepository;
        this.equipmentConverter = equipmentConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Equipment equipment) {
        return ItemDomainId.builder()
            .itemName(equipment.getItemName())
            .externalReference(equipment.getExternalReference())
            .locationType(equipment.getLocationType())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Equipment> bufferCopy) {
        List<EquipmentEntity> entities = new ArrayList<>(equipmentConverter.convertDomain(bufferCopy));
        List<String> itemNames = entities.stream().map(EquipmentEntity::getItemName).distinct().toList();
        List<String> stationIds = entities.stream().map(EquipmentEntity::getStationId).filter(id -> id != null).distinct().toList();
        List<String> fleetCarrierIds = entities.stream().map(EquipmentEntity::getFleetCarrierId).filter(id -> id != null).distinct().toList();

        Map<BiWrapper<String, String>, EquipmentEntity> existingStations = stationIds.isEmpty()
            ? Map.of()
            : equipmentRepository.getByStationIdInAndItemNameIn(stationIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getStationId(), entity.getItemName()), Function.identity()));
        Map<BiWrapper<String, String>, EquipmentEntity> existingFleetCarriers = fleetCarrierIds.isEmpty()
            ? Map.of()
            : equipmentRepository.getByFleetCarrierIdInAndItemNameIn(fleetCarrierIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getFleetCarrierId(), entity.getItemName()), Function.identity()));

        entities.forEach(entity -> {
            BiWrapper<String, String> key = new BiWrapper<>(
                entity.getStationId() == null ? entity.getFleetCarrierId() : entity.getStationId(),
                entity.getItemName()
            );
            EquipmentEntity existing = entity.getStationId() == null
                ? existingFleetCarriers.get(key)
                : existingStations.get(key);
            if (existing != null) {
                entity.setId(existing.getId());
            }
        });

        try {
            equipmentRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving Equipment batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    equipmentRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving Equipment", e2);
                }
            });
        }
    }
}
