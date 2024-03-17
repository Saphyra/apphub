package com.github.saphyra.integration.server.domain.test_run;

import com.github.saphyra.integration.server.api.test_run.TestRunStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TestRunRepository extends CrudRepository<TestRun, UUID> {
    List<TestRun> getByStatus(TestRunStatus status);

    List<TestRun> getByEndTimeBefore(OffsetDateTime expirationTime);
}
