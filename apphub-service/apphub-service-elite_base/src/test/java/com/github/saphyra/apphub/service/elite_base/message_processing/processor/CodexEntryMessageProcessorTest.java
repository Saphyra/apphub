package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.codex_entry.CodexEntryMessage;
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
class CodexEntryMessageProcessorTest {
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final Long STAR_ID = 1341L;
    private static final String STAR_NAME = "star-name";
    private static final Double[] STAR_POSITION = new Double[]{34.23};
    private static final String MESSAGE = "message";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private StarSystemSaver starSystemSaver;

    @InjectMocks
    private CodexEntryMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Test
    void canProcess() {
        given(edMessage.getSchemaRef()).willReturn(SchemaRefs.CODEX_ENTRY);

        assertThat(underTest.canProcess(edMessage)).isTrue();
    }

    @Test
    void processMessage() {
        CodexEntryMessage codexEntryMessage = CodexEntryMessage.builder()
            .timestamp(TIMESTAMP)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .starPosition(STAR_POSITION)
            .build();

        given(edMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.readValue(MESSAGE, CodexEntryMessage.class)).willReturn(codexEntryMessage);

        underTest.processMessage(edMessage);

        then(starSystemSaver).should().save(TIMESTAMP, STAR_ID, STAR_NAME, STAR_POSITION);
    }
}