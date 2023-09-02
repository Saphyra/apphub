package com.github.saphyra.integration.server.domain.test_run;

import com.github.saphyra.integration.server.api.test_run.TestRunStatus;
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
import org.hibernate.annotations.UpdateTimestamp;

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

    @UpdateTimestamp
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TestRunStatus status;
}
