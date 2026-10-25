package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity;

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
class CommodityWriteBuffer extends WriteBuffer<ItemDomainId, Commodity> {
    private final CommodityRepository commodityRepository;
    private final CommodityConverter commodityConverter;
    private final ErrorReporterService errorReporterService;

    protected CommodityWriteBuffer(DateTimeUtil dateTimeUtil, CommodityRepository commodityRepository, CommodityConverter commodityConverter, ErrorReporterService errorReporterService) {
        super(dateTimeUtil);
        this.commodityRepository = commodityRepository;
        this.commodityConverter = commodityConverter;
        this.errorReporterService = errorReporterService;
    }

    @Override
    protected ItemDomainId getDomainId(Commodity commodity) {
        return ItemDomainId.builder()
            .itemName(commodity.getItemName())
            .externalReference(commodity.getExternalReference())
            .locationType(commodity.getLocationType())
            .build();
    }

    @Override
    protected void doSynchronize(Collection<Commodity> bufferCopy) {
        List<CommodityEntity> entities = new ArrayList<>(commodityConverter.convertDomain(bufferCopy));
        List<String> itemNames = entities.stream().map(CommodityEntity::getItemName).distinct().toList();
        List<String> stationIds = entities.stream().map(CommodityEntity::getStationId).filter(id -> id != null).distinct().toList();
        List<String> fleetCarrierIds = entities.stream().map(CommodityEntity::getFleetCarrierId).filter(id -> id != null).distinct().toList();

        Map<BiWrapper<String, String>, CommodityEntity> existingStations = stationIds.isEmpty()
            ? Map.of()
            : commodityRepository.getByStationIdInAndItemNameIn(stationIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getStationId(), entity.getItemName()), Function.identity()));
        Map<BiWrapper<String, String>, CommodityEntity> existingFleetCarriers = fleetCarrierIds.isEmpty()
            ? Map.of()
            : commodityRepository.getByFleetCarrierIdInAndItemNameIn(fleetCarrierIds, itemNames).stream()
                .collect(Collectors.toMap(entity -> new BiWrapper<>(entity.getFleetCarrierId(), entity.getItemName()), Function.identity()));

        entities.forEach(entity -> {
            BiWrapper<String, String> key = new BiWrapper<>(
                entity.getStationId() == null ? entity.getFleetCarrierId() : entity.getStationId(),
                entity.getItemName()
            );
            CommodityEntity existing = entity.getStationId() == null
                ? existingFleetCarriers.get(key)
                : existingStations.get(key);
            if (existing != null) {
                entity.setId(existing.getId());
            }
        });

        try {
            commodityRepository.saveAll(entities);
        } catch (Exception e) {
            entities.forEach(entity -> {
                try {
                    commodityRepository.save(entity);
                } catch (Exception e2) {
                    errorReporterService.report("Failed saving Commodity", e2);
                }
            });
        }
    }
}
