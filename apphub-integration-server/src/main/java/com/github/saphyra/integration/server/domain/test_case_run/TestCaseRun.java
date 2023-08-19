package com.github.saphyra.integration.server.domain.test_case_run;

import com.github.saphyra.integration.server.api.test_case.request.TestCaseRunStatus;
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
@Table(name = "test_case_run")
public class TestCaseRun {
    @Id
    private UUID id;

    private UUID testRunId;
    private String testCaseId;
    private Long duration;
    @Enumerated(EnumType.STRING)
    private TestCaseRunStatus status;
    @CreationTimestamp
    private OffsetDateTime createdAt;
}
