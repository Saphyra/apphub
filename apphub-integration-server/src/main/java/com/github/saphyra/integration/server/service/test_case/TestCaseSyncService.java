package com.github.saphyra.integration.server.service.test_case;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.integration.server.api.test_case.request.TestCaseRequest;
import com.github.saphyra.integration.server.domain.test_case.TestCase;
import com.github.saphyra.integration.server.domain.test_case.TestCasreRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseSyncService {
    private final TestCasreRepository testCasreRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public synchronized void createOrUpdate(TestCaseRequest request) {
        TestCase testCase = testCasreRepository.findById(request.getId())
            .orElseGet(() -> TestCase.builder()
                .id(request.getId())
                .name(request.getName())
                .build());

        testCase.setGroups(objectMapper.writeValueAsString(request.getGroups()));

        testCasreRepository.save(testCase);
    }
}
