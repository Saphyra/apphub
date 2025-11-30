package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_body_signals.FssBodySignalsMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FssBodySignalsMessageProcessorTest {
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Long STAR_ID = 3242L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{342.432};
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long BODY_ID = 245L;
    private static final String BODY_NAME = "body-name";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @Mock
    private BodySaver bodySaver;

    @InjectMocks
    private FssBodySignalsMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private StarSystem starSystem;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.FSS_BODY_SIGNALS);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        FssBodySignalsMessage fssAllBodiesFoundMessage = FssBodySignalsMessage.builder()
            .timestamp(TIMESTAMP)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .starPosition(STAR_POSITION)
            .bodyId(BODY_ID)
            .bodyName(BODY_NAME)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapper.readValue(MESSAGE, FssBodySignalsMessage.class)).willReturn(fssAllBodiesFoundMessage);
        given(starSystemSaver.save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION)).willReturn(starSystem);
        given(starSystem.getId()).willReturn(STAR_SYSTEM_ID);

        underTest.processMessage(edMessage);

        then(bodySaver).should().save(TIMESTAMP, STAR_SYSTEM_ID, null, BODY_ID, BODY_NAME);
    }
}