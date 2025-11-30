package com.github.saphyra.apphub.service.custom.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ControllingFactionParserTest {
    private static final Object VALUE = "value";

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ControllingFactionParser underTest;

    @Test
    void nullInput() {
        assertThat(underTest.parse(null)).isNull();
    }

    @Test
    void parseString() {
        assertThat(underTest.parse(VALUE))
            .returns(VALUE, ControllingFaction::getFactionName);
    }

    @Test
    void parse() {
        ControllingFaction controllingFaction = ControllingFaction.builder()
            .factionName(VALUE.toString())
            .build();

        assertThat(underTest.parse(controllingFaction)).isEqualTo(controllingFaction);
    }
}