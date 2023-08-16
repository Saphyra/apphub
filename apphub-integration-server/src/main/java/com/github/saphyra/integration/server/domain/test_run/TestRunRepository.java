package com.github.saphyra.integration.server.domain.test_run;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TestRunRepository extends CrudRepository<TestRun, UUID> {
}
