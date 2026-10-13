package com.github.saphyra.apphub.ci.dao.integration_server.test_run;

import com.github.saphyra.apphub.ci.api.model.integration_server.TestRunStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TestRunRepository extends CrudRepository<TestRun, UUID> {
    List<TestRun> getByStatus(TestRunStatus status);

    List<TestRun> getByEndTimeBefore(OffsetDateTime expirationTime);
}
