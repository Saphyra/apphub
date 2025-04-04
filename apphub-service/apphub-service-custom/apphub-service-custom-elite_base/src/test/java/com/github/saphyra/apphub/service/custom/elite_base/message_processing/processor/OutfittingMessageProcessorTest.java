package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.outfitting.OutfittingMessage;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class OutfittingMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String STAR_NAME = "star-name";
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long MARKET_ID = 2352L;
    private static final String STATION_NAME = "station-name";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String MODULE = "module";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private StationSaverUtil stationSaverUtil;

    @Mock
    private LoadoutSaver loadoutSaver;

    @Mock
    private PerformanceReporter performanceReporter;

    @InjectMocks
    private OutfittingMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private StarSystem starSystem;

    @Mock
    private StationSaveResult saveResult;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.OUTFITTING);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage_nullExternalReference(){
        String[] modules = new String[]{MODULE};

        OutfittingMessage outfittingMessage = OutfittingMessage.builder()
            .timestamp(TIMESTAMP)
            .systemName(STAR_NAME)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .modules(modules)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, OutfittingMessage.class)).willReturn(outfittingMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(TIMESTAMP, STAR_SYSTEM_ID, MARKET_ID, STATION_NAME)).willReturn(saveResult);
        given(saveResult.getExternalReference()).willReturn(null);
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());

        assertThat(catchThrowable(() -> underTest.processMessage(edMessage))).isInstanceOf(MessageProcessingDelayedException.class);
    }

    @Test
    void processMessage() {
        String[] modules = new String[]{MODULE};

        OutfittingMessage outfittingMessage = OutfittingMessage.builder()
            .timestamp(TIMESTAMP)
            .systemName(STAR_NAME)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .modules(modules)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, OutfittingMessage.class)).willReturn(outfittingMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(TIMESTAMP, STAR_SYSTEM_ID, MARKET_ID, STATION_NAME)).willReturn(saveResult);
        given(saveResult.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(saveResult.getCommodityLocation()).willReturn(CommodityLocation.STATION);
        given(performanceReporter.wrap(any(Callable.class), any(), any())).willAnswer(invocation -> invocation.getArgument(0, Callable.class).call());
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(), any());

        underTest.processMessage(edMessage);

        then(loadoutSaver).should().save(TIMESTAMP, LoadoutType.OUTFITTING, CommodityLocation.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(MODULE));
    }
}