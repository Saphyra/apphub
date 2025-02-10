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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FcMaterialsJournalMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String CARRIER_ID = "carrier-id";
    private static final String CARRIER_NAME = "carrier-name";
    private static final Long MARKET_ID = 343L;
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer PRICE = 32143;
    private static final Integer STOCK = 56;
    private static final Integer DEMAND = 67;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private FleetCarrierSaver fleetCarrierSaver;

    @Mock
    private CommoditySaver commoditySaver;

    @InjectMocks
    private FcMaterialsJournalMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private FleetCarrier fleetCarrier;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.FC_MATERIALS_JOURNAL);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void process() {
        FcMaterialsJournalMessage fcMaterialsJournalMessage = FcMaterialsJournalMessage.builder()
            .timestamp(TIMESTAMP)
            .carrierId(CARRIER_ID)
            .carrierName(CARRIER_NAME)
            .marketId(MARKET_ID)
            .items(new FcMaterialJournalItem[]{FcMaterialJournalItem.builder()
                .name(NAME)
                .price(PRICE)
                .stock(STOCK)
                .demand(DEMAND)
                .build()})
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, FcMaterialsJournalMessage.class)).willReturn(fcMaterialsJournalMessage);
        given(fleetCarrierSaver.save(TIMESTAMP, CARRIER_ID, CARRIER_NAME, MARKET_ID)).willReturn(fleetCarrier);
        given(fleetCarrier.getId()).willReturn(FLEET_CARRIER_ID);

        underTest.processMessage(edMessage);

        then(commoditySaver).should().saveAll(
            TIMESTAMP,
            CommodityType.FC_MATERIAL,
            CommodityLocation.FLEET_CARRIER,
            FLEET_CARRIER_ID,
            MARKET_ID,
            List.of(CommoditySaver.CommodityData.builder()
                .name(NAME)
                .buyPrice(PRICE)
                .sellPrice(PRICE)
                .stock(STOCK)
                .demand(DEMAND)
                .build())
        );
    }
}