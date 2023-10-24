package com.github.saphyra.apphub.service.admin_panel.migration_task.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MigrationTaskConverterTest {
    private static final String EVENT = "event";
    private static final String NAME = "name";

    @InjectMocks
    private MigrationTaskConverter underTest;

    @Test
    void convertDomain() {
        MigrationTask domain = MigrationTask.builder()
            .event(EVENT)
            .name(NAME)
            .completed(true)
            .build();

        MigrationTaskEntity result = underTest.convertDomain(domain);

        assertThat(result.getEvent()).isEqualTo(EVENT);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getCompleted()).isTrue();
    }

    @Test
    void convertEntity() {
        MigrationTaskEntity domain = MigrationTaskEntity.builder()
            .event(EVENT)
            .name(NAME)
            .completed(true)
            .build();

        MigrationTask result = underTest.convertEntity(domain);

        assertThat(result.getEvent()).isEqualTo(EVENT);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getCompleted()).isTrue();
    }
}