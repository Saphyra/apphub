package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.FleetCarrierSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi.FcMaterialCapiItems;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi.FcMaterialsCapiMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FcMaterialsCapiMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FleetCarrierSaver fleetCarrierSaver;
    private final CommoditySaver commoditySaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FC_MATERIALS_CAPI.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FcMaterialsCapiMessage fcMaterialsCapiMessage = objectMapperWrapper.readValue(message.getMessage(), FcMaterialsCapiMessage.class);

        FleetCarrier fleetCarrier = fleetCarrierSaver.save(fcMaterialsCapiMessage.getTimestamp(), fcMaterialsCapiMessage.getCarrierId(), fcMaterialsCapiMessage.getMarketId());

        commoditySaver.saveAll(
            fcMaterialsCapiMessage.getTimestamp(),
            CommodityType.FC_MATERIAL,
            CommodityLocation.FLEET_CARRIER,
            fleetCarrier.getId(),
            fcMaterialsCapiMessage.getMarketId(),
            getMaterials(fcMaterialsCapiMessage.getItems())
        );
    }

    private List<CommoditySaver.CommodityData> getMaterials(FcMaterialCapiItems items) {
        List<CommoditySaver.CommodityData> purchases = new ArrayList<>();
        if (!items.getPurchases().toString().equals("[]")) {
            TypeReference<List<Purchase>> typeReference = new TypeReference<>() {
            };

            List<Purchase> purchaseList = objectMapperWrapper.convertValue(items.getPurchases(), typeReference);

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

            Map<Long, Sale> saleMap = objectMapperWrapper.convertValue(items.getSales(), typeReference);

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
    private static class Purchase {
        private String name;
        private Integer price;
        private Integer outstanding;
        private Integer total;
    }

    @NoArgsConstructor
    @Data
    private static class Sale {
        private Long id;
        private String name;
        private Integer price;
        private Integer stock;
    }
}
