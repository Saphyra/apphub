package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CitizenAllocationsTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final CitizenAllocations underTest = new CitizenAllocations();

    @Test
    public void releaseByProcessId() {
        underTest.put(CITIZEN_ID, PROCESS_ID);

        underTest.releaseByProcessId(PROCESS_ID);

        assertThat(underTest).isEmpty();
    }

    @Test
    public void findByProcessId() {
        underTest.put(CITIZEN_ID, PROCESS_ID);

        Optional<UUID> result = underTest.findByProcessId(PROCESS_ID);

        assertThat(result).contains(CITIZEN_ID);
    }
}