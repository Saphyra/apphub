package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.docking_granted.DockingGrantedMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DockingGrantedMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String STATION_TYPE = "station-type";
    private static final Long MARKET_ID = 214L;
    private static final String STATION_NAME = "station-name";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StationSaverUtil stationSaverUtil;

    @InjectMocks
    private DockingGrantedMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.DOCKING_GRANTED);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        DockingGrantedMessage dockingDeniedMessage = DockingGrantedMessage.builder()
            .timestamp(TIMESTAMP)
            .stationType(STATION_TYPE)
            .marketId(MARKET_ID)
            .stationName(STATION_NAME)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, DockingGrantedMessage.class)).willReturn(dockingDeniedMessage);

        underTest.processMessage(edMessage);

        then(stationSaverUtil).should().saveStationOrFleetCarrier(TIMESTAMP, STATION_TYPE, MARKET_ID, STATION_NAME);
    }
}