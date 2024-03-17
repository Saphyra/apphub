package com.github.saphyra.integration.server.domain.test_case;

import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface TestCaseRepository extends CrudRepository<TestCase, String> {
    List<TestCase> getByLastRunBefore(OffsetDateTime expirationTime);
}
