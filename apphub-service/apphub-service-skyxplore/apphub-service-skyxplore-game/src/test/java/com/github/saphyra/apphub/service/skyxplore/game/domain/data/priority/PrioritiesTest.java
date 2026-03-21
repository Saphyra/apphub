package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PrioritiesTest {
    private static final UUID LOCATION = UUID.randomUUID();

    @InjectMocks
    private Priorities underTest;

    @Test
    void findByLocationAndType() {
        Priority differentLocation = Priority.builder()
            .location(UUID.randomUUID())
            .type(PriorityType.CONSTRUCTION)
            .build();
        Priority differentType = Priority.builder()
            .location(LOCATION)
            .type(PriorityType.INDUSTRY)
            .build();
        Priority priority = Priority.builder()
            .location(LOCATION)
            .type(PriorityType.CONSTRUCTION)
            .build();

        underTest.add(differentLocation);
        underTest.add(differentType);
        underTest.add(priority);

        assertThat(underTest.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).isEqualTo(priority);
    }

    @Test
    void getByLocation() {
        Priority differentLocation = Priority.builder()
            .location(UUID.randomUUID())
            .build();
        Priority priority = Priority.builder()
            .location(LOCATION)
            .build();

        underTest.add(differentLocation);
        underTest.add(priority);

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(priority);
    }

    @Test
    void setExisting() {
        Priority priority = Priority.builder()
            .existing(false)
            .build();

        underTest.add(priority);

        underTest.setExisting();

        assertThat(priority.isExisting()).isTrue();
    }
}