package com.github.saphyra.integration.server.domain.test_case_run;

import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TestCaseRunRepository extends CrudRepository<TestCaseRun, UUID> {
    List<TestCaseRun> getByTestCaseId(String testCaseId);

    List<TestCaseRun> getByCreatedAtBefore(OffsetDateTime expirationTime);
}
