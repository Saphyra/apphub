package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_journal.FcMaterialJournalItem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_journal.FcMaterialsJournalMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.FleetCarrierSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class FcMaterialsJournalMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FleetCarrierSaver fleetCarrierSaver;
    private final CommoditySaver commoditySaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FC_MATERIALS_JOURNAL.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FcMaterialsJournalMessage fcMaterialsJournalMessage = objectMapperWrapper.readValue(message.getMessage(), FcMaterialsJournalMessage.class);

        FleetCarrier fleetCarrier = fleetCarrierSaver.save(
            fcMaterialsJournalMessage.getTimestamp(),
            fcMaterialsJournalMessage.getCarrierId(),
            fcMaterialsJournalMessage.getCarrierName(),
            fcMaterialsJournalMessage.getMarketId()
        );

        commoditySaver.saveAll(
            fcMaterialsJournalMessage.getTimestamp(),
            CommodityType.FC_MATERIAL,
            CommodityLocation.FLEET_CARRIER,
            fleetCarrier.getId(),
            fcMaterialsJournalMessage.getMarketId(),
            getMaterials(fcMaterialsJournalMessage.getItems())
        );
    }

    private List<CommoditySaver.CommodityData> getMaterials(FcMaterialJournalItem[] items) {
        return Arrays.stream(items)
            .map(item -> CommoditySaver.CommodityData.builder()
                .name(item.getName())
                .buyPrice(item.getPrice())
                .sellPrice(item.getPrice())
                .stock(item.getStock())
                .demand(item.getDemand())
                .build())
            .toList();
    }
}
