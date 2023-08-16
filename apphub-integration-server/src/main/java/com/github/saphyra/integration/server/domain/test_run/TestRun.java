package com.github.saphyra.integration.server.domain.test_run;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "test_run")
public class TestRun {
    @Id
    private UUID id;

    @CreationTimestamp
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TestRunStatus status;
}
