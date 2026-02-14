package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_capi.FcMaterialCapiItems;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_capi.FcMaterialsCapiMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
class FcMaterialsCapiMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final CommoditySaver commoditySaver;
    private final FleetCarrierDao fleetCarrierDao;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FC_MATERIALS_CAPI.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FcMaterialsCapiMessage fcMaterialsCapiMessage = objectMapper.readValue(message.getMessage(), FcMaterialsCapiMessage.class);

        fleetCarrierDao.findByCarrierId(fcMaterialsCapiMessage.getCarrierId())
            .ifPresent(fleetCarrier -> commoditySaver.saveAll(
                fcMaterialsCapiMessage.getTimestamp(),
                ItemType.FC_MATERIAL,
                ItemLocationType.FLEET_CARRIER,
                fleetCarrier.getId(),
                fcMaterialsCapiMessage.getMarketId(),
                getMaterials(fcMaterialsCapiMessage.getItems())
            ));
    }

    private List<CommoditySaver.CommodityData> getMaterials(FcMaterialCapiItems items) {
        List<CommoditySaver.CommodityData> purchases = new ArrayList<>();
        if (!items.getPurchases().toString().equals("[]")) {
            TypeReference<List<Purchase>> typeReference = new TypeReference<>() {
            };

            List<Purchase> purchaseList = objectMapper.convertValue(items.getPurchases(), typeReference);

            purchaseList.stream()
                .map(purchase -> CommoditySaver.CommodityData.builder()
                    .name(purchase.getName())
                    .demand(purchase.getOutstanding())
                    .buyPrice(purchase.getPrice())
                    .build())
                .forEach(purchases::add);
        }

        List<CommoditySaver.CommodityData> sales = new ArrayList<>();
        if (!items.getSales().toString().equals("[]")) {
            TypeReference<Map<Long, Sale>> typeReference = new TypeReference<>() {
            };

            Map<Long, Sale> saleMap = objectMapper.convertValue(items.getSales(), typeReference);

            saleMap.values()
                .stream()
                .map(sale -> CommoditySaver.CommodityData.builder()
                    .name(sale.getName())
                    .stock(sale.getStock())
                    .sellPrice(sale.getPrice())
                    .build())
                .forEach(sales::add);
        }
        return Stream.concat(purchases.stream(), sales.stream())
            .collect(Collectors.toList());
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    static class Purchase {
        private String name;
        private Integer price;
        private Integer outstanding;
        private Integer total;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    static class Sale {
        private Long id;
        private String name;
        private Integer price;
        private Integer stock;
    }
}
