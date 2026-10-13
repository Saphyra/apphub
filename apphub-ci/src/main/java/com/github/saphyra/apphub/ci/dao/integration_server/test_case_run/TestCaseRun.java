package com.github.saphyra.apphub.ci.dao.integration_server.test_case_run;

import com.github.saphyra.apphub.ci.api.model.integration_server.TestCaseRunStatus;
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

import static com.github.saphyra.apphub.ci.dao.DaoConstants.SCHEMA_INTEGRATION_SERVER;
import static com.github.saphyra.apphub.ci.dao.DaoConstants.TABLE_TEST_CASE_RUN;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = SCHEMA_INTEGRATION_SERVER, name = TABLE_TEST_CASE_RUN)
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
