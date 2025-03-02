package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity.CommodityMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity.EdCommodity;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class CommodityMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String SYSTEM_NAME = "system-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STATION_TYPE = "station-type";
    private static final Long MARKET_ID = 324L;
    private static final String STATION_NAME = "station-name";
    private static final String DOCKING_ACCESS = "docking-access";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private CommoditySaver commoditySaver;

    @Mock
    private StationSaverUtil stationSaverUtil;

    @Mock
    private PerformanceReporter performanceReporter;

    @InjectMocks
    private CommodityMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private StarSystem starSystem;

    @Mock
    private EdCommodity edCommodity;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.COMMODITY);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage_unknownCommodityLocation() {
        CommodityMessage commodityMessage = CommodityMessage.builder()
            .timestamp(TIMESTAMP)
            .systemName(SYSTEM_NAME)
            .stationType(STATION_TYPE)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .carrierDockingAccess(DOCKING_ACCESS)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, CommodityMessage.class)).willReturn(commodityMessage);
        given(starSystemSaver.save(TIMESTAMP, SYSTEM_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(
            TIMESTAMP,
            STAR_SYSTEM_ID,
            null,
            STATION_TYPE,
            MARKET_ID,
            STATION_NAME,
            null,
            null,
            null,
            null,
            DOCKING_ACCESS,
            null
        ))
            .willReturn(StationSaveResult.builder().commodityLocation(CommodityLocation.UNKNOWN).build());
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());

        assertThat(catchThrowable(() -> underTest.processMessage(edMessage))).isInstanceOf(MessageProcessingDelayedException.class);
    }

    @Test
    void processMessage() {
        EdCommodity[] commodities = new EdCommodity[]{edCommodity};

        CommodityMessage commodityMessage = CommodityMessage.builder()
            .timestamp(TIMESTAMP)
            .systemName(SYSTEM_NAME)
            .stationType(STATION_TYPE)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .carrierDockingAccess(DOCKING_ACCESS)
            .commodities(commodities)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, CommodityMessage.class)).willReturn(commodityMessage);
        given(starSystemSaver.save(TIMESTAMP, SYSTEM_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(
            TIMESTAMP,
            STAR_SYSTEM_ID,
            null,
            STATION_TYPE,
            MARKET_ID,
            STATION_NAME,
            null,
            null,
            null,
            null,
            DOCKING_ACCESS,
            null
        ))
            .willReturn(StationSaveResult.builder().commodityLocation(CommodityLocation.STATION).externalReference(EXTERNAL_REFERENCE).build());
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(commoditySaver).should().saveAll(TIMESTAMP, CommodityType.COMMODITY, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, commodities);
    }
}