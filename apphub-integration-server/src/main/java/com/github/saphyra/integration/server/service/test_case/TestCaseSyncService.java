package com.github.saphyra.integration.server.service.test_case;

import com.github.saphyra.integration.server.api.test_case.request.TestCaseRequest;
import com.github.saphyra.integration.server.domain.test_method.TestCase;
import com.github.saphyra.integration.server.domain.test_method.TestCasreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseSyncService {
    private final TestCasreRepository testCasreRepository;

    public void createOrUpdate(TestCaseRequest request) {
        TestCase testCase = testCasreRepository.findById(request.getId())
            .orElseGet(() -> TestCase.builder()
                .id(request.getId())
                .build());

        testCase.setGroups(request.getGroups());

        testCasreRepository.save(testCase);
    }
}
