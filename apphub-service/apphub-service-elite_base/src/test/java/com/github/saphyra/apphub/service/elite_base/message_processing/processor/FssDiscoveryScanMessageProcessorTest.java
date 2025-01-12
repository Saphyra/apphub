package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fss_discovery_scan.FssDiscoveryScanMessage;
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
class FssDiscoveryScanMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Long STAR_ID = 234L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{343.4};

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @InjectMocks
    private FssDiscoveryScanMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.FSS_DISCOVERY_SCAN);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        FssDiscoveryScanMessage fssDiscoveryScanMessage = FssDiscoveryScanMessage.builder()
            .timestamp(TIMESTAMP)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .starPosition(STAR_POSITION)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, FssDiscoveryScanMessage.class)).willReturn(fssDiscoveryScanMessage);

        underTest.processMessage(edMessage);

        then(starSystemSaver).should().save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION);
    }
}