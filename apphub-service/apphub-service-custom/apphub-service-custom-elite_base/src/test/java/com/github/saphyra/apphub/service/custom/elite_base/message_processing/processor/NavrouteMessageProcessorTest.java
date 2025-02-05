package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system.StarType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.navroute.NavrouteMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.navroute.Waypoint;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
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
class NavrouteMessageProcessorTest {
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
    private NavrouteMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.NAVROUTE);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        NavrouteMessage navrouteMessage = NavrouteMessage.builder()
            .timestamp(TIMESTAMP)
            .route(new Waypoint[]{Waypoint.builder()
                .starId(STAR_ID)
                .starName(STAR_NAME)
                .starPosition(STAR_POSITION)
                .starType("a")
                .build()})
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, NavrouteMessage.class)).willReturn(navrouteMessage);

        underTest.processMessage(edMessage);

        then(starSystemSaver).should().save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION, StarType.A);
    }
}