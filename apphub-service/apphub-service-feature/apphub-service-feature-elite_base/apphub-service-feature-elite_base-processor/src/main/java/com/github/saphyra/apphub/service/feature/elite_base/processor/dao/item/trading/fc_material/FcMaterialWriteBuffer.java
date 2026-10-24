package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material;

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
class FcMaterialWriteBuffer extends WriteBuffer<ItemDomainId, FcMaterial> {
    private final FcMaterialRepository fcMaterialRepository;
    private final FcMaterialConverter fcMaterialConverter;
    private final ErrorReporterService errorReporterService;

    protected FcMaterialWriteBuffer(DateTimeUtil dateTimeUtil, FcMaterialRepository fcMaterialRepository, FcMaterialConverter fcMaterialConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.fcMaterialRepository = fcMaterialRepository;
        this.fcMaterialConverter = fcMaterialConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(FcMaterial commodity) {
        return ItemDomainId.builder()
            .itemName(commodity.getItemName())
            .externalReference(commodity.getExternalReference())
            .locationType(commodity.getLocationType())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<FcMaterial> bufferCopy) {
        List<FcMaterialEntity> entities = new ArrayList<>(fcMaterialConverter.convertDomain(bufferCopy));
        List<String> itemNames = entities.stream().map(FcMaterialEntity::getItemName).distinct().toList();
        List<String> stationIds = entities.stream().map(FcMaterialEntity::getStationId).filter(id -> id != null).distinct().toList();
        List<String> fleetCarrierIds = entities.stream().map(FcMaterialEntity::getFleetCarrierId).filter(id -> id != null).distinct().toList();

        Map<BiWrapper<String, String>, FcMaterialEntity> existingStations = stationIds.isEmpty()
            ? Map.of()
            : fcMaterialRepository.getByStationIdInAndItemNameIn(stationIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getStationId(), entity.getItemName()), Function.identity()));
        Map<BiWrapper<String, String>, FcMaterialEntity> existingFleetCarriers = fleetCarrierIds.isEmpty()
            ? Map.of()
            : fcMaterialRepository.getByFleetCarrierIdInAndItemNameIn(fleetCarrierIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getFleetCarrierId(), entity.getItemName()), Function.identity()));

        entities.forEach(entity -> {
            BiWrapper<String, String> key = new BiWrapper<>(
                entity.getStationId() == null ? entity.getFleetCarrierId() : entity.getStationId(),
                entity.getItemName()
            );
            FcMaterialEntity existing = entity.getStationId() == null
                ? existingFleetCarriers.get(key)
                : existingStations.get(key);
            if (existing != null) {
                entity.setId(existing.getId());
            }
        });

        try {
            fcMaterialRepository.saveAll(entities);
        } catch (Exception e) {
            errorReporterService.report("Failed saving FcMaterial batch. Trying one by one", e);
            entities.forEach(entity -> {
                try {
                    fcMaterialRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving FcMaterial", e2);
                }
            });
        }
    }
}
