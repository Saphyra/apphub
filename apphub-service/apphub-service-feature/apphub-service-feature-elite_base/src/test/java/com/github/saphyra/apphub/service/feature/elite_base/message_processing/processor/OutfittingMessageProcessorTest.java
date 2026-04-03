package com.github.saphyra.apphub.service.feature.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import com.github.saphyra.apphub.lib.monitoring.instrument.MetricMapper;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.outfitting.OutfittingMessage;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.util.StationSaverUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

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
    private ObjectMapper objectMapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private StationSaverUtil stationSaverUtil;

    @Mock
    private LoadoutSaver loadoutSaver;

    @Spy
    private final MonitoringInstruments monitoringInstruments = new MonitoringInstruments(mock(MetricMapper.class), mock(MetricRegistry.class));

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
    void processMessage_nullExternalReference() {
        String[] modules = new String[]{MODULE};

        OutfittingMessage outfittingMessage = OutfittingMessage.builder()
            .timestamp(TIMESTAMP)
            .systemName(STAR_NAME)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .modules(modules)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapper.readValue(MESSAGE, OutfittingMessage.class)).willReturn(outfittingMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(TIMESTAMP, STAR_SYSTEM_ID, MARKET_ID, STATION_NAME)).willReturn(saveResult);
        given(saveResult.getExternalReference()).willReturn(null);

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
        given(objectMapper.readValue(MESSAGE, OutfittingMessage.class)).willReturn(outfittingMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_NAME)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);
        given(stationSaverUtil.saveStationOrFleetCarrier(TIMESTAMP, STAR_SYSTEM_ID, MARKET_ID, STATION_NAME)).willReturn(saveResult);
        given(saveResult.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(saveResult.getLocationType()).willReturn(ItemLocationType.STATION);

        underTest.processMessage(edMessage);

        then(loadoutSaver).should().save(TIMESTAMP, ItemType.EQUIPMENT, ItemLocationType.STATION, EXTERNAL_REFERENCE, MARKET_ID, List.of(MODULE));
    }
}