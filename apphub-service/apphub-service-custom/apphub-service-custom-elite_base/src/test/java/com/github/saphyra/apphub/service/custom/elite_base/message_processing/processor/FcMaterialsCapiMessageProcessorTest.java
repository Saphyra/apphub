package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrier;
import com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier.FleetCarrierDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_capi.FcMaterialCapiItems;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_capi.FcMaterialsCapiMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FcMaterialsCapiMessageProcessorTest {
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String CARRIER_ID = "carrier-id";
    private static final Long MARKET_ID = 342L;
    private static final String MESSAGE = "message";
    private static final UUID FLEET_CARRIER_ID = UUID.randomUUID();
    private static final String PURCHASE = "purchase";
    private static final Integer BUY_PRICE = 536;
    private static final Integer DEMAND = 43;
    private static final String SALE = "sale";
    private static final Integer SELL_PRICE = 3425;
    private static final Integer STOCK = 34637;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FleetCarrierDao fleetCarrierDao;

    @Mock
    private CommoditySaver commoditySaver;

    private FcMaterialsCapiMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private FleetCarrier fleetCarrier;

    @Captor
    private ArgumentCaptor<List<CommoditySaver.CommodityData>> argumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = FcMaterialsCapiMessageProcessor.builder()
            .objectMapper(objectMapper)
            .fleetCarrierDao(fleetCarrierDao)
            .commoditySaver(commoditySaver)
            .build();
    }

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.FC_MATERIALS_CAPI);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void noTrades() {
        FcMaterialsCapiMessage fcMaterialsCapiMessage = FcMaterialsCapiMessage.builder()
            .timestamp(TIMESTAMP)
            .carrierId(CARRIER_ID)
            .marketId(MARKET_ID)
            .items(FcMaterialCapiItems.builder()
                .purchases("[]")
                .sales("[]")
                .build())
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapper.readValue(MESSAGE, FcMaterialsCapiMessage.class)).willReturn(fcMaterialsCapiMessage);
        given(fleetCarrierDao.findByCarrierId(CARRIER_ID)).willReturn(Optional.of(fleetCarrier));
        given(fleetCarrier.getId()).willReturn(FLEET_CARRIER_ID);

        underTest.processMessage(edMessage);

        then(commoditySaver).should().saveAll(TIMESTAMP, ItemType.FC_MATERIAL, ItemLocationType.FLEET_CARRIER, FLEET_CARRIER_ID, MARKET_ID, Collections.emptyList());
    }

    @Test
    void fleetCarrierNotFound() {
        FcMaterialsCapiMessage fcMaterialsCapiMessage = FcMaterialsCapiMessage.builder()
            .timestamp(TIMESTAMP)
            .carrierId(CARRIER_ID)
            .marketId(MARKET_ID)
            .items(FcMaterialCapiItems.builder()
                .purchases("[]")
                .sales("[]")
                .build())
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapper.readValue(MESSAGE, FcMaterialsCapiMessage.class)).willReturn(fcMaterialsCapiMessage);
        given(fleetCarrierDao.findByCarrierId(CARRIER_ID)).willReturn(Optional.empty());

        underTest.processMessage(edMessage);

        then(commoditySaver).shouldHaveNoInteractions();
    }

    @Test
    void processMessage() {
        FcMaterialsCapiMessageProcessor.Purchase purchase = FcMaterialsCapiMessageProcessor.Purchase.builder()
            .name(PURCHASE)
            .price(BUY_PRICE)
            .outstanding(DEMAND)
            .build();
        FcMaterialsCapiMessageProcessor.Sale sale = FcMaterialsCapiMessageProcessor.Sale.builder()
            .name(SALE)
            .price(SELL_PRICE)
            .stock(STOCK)
            .build();
        FcMaterialsCapiMessage fcMaterialsCapiMessage = FcMaterialsCapiMessage.builder()
            .timestamp(TIMESTAMP)
            .carrierId(CARRIER_ID)
            .marketId(MARKET_ID)
            .items(FcMaterialCapiItems.builder()
                .purchases(List.of(purchase))
                .sales(Map.of(1L, sale))
                .build())
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapper.readValue(MESSAGE, FcMaterialsCapiMessage.class)).willReturn(fcMaterialsCapiMessage);
        given(fleetCarrierDao.findByCarrierId(CARRIER_ID)).willReturn(Optional.of(fleetCarrier));
        given(fleetCarrier.getId()).willReturn(FLEET_CARRIER_ID);
        given(objectMapper.convertValue(eq(List.of(purchase)), any(TypeReference.class))).willReturn(List.of(purchase));
        given(objectMapper.convertValue(eq(Map.of(1L, sale)), any(TypeReference.class))).willReturn(Map.of(1L, sale));

        underTest.processMessage(edMessage);

        then(commoditySaver).should().saveAll(eq(TIMESTAMP), eq(ItemType.FC_MATERIAL), eq(ItemLocationType.FLEET_CARRIER), eq(FLEET_CARRIER_ID), eq(MARKET_ID), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(
            CommoditySaver.CommodityData.builder()
                .name(PURCHASE)
                .demand(DEMAND)
                .buyPrice(BUY_PRICE)
                .build(),
            CommoditySaver.CommodityData.builder()
                .name(SALE)
                .stock(STOCK)
                .sellPrice(SELL_PRICE)
                .build()
        );
    }
}