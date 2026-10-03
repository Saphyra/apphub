package com.github.saphyra.apphub.ci.service.integration_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.ci.api.model.integration_server.TestCaseRequest;
import com.github.saphyra.apphub.ci.dao.integration_server.test_case.TestCase;
import com.github.saphyra.apphub.ci.dao.integration_server.test_case.TestCaseRepository;
import com.github.saphyra.apphub.ci.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestCaseSyncService {
    private final TestCaseRepository testCaseRepository;
    private final ObjectMapper objectMapper;
    private final DateTimeUtil dateTimeUtil;

    @SneakyThrows
    public synchronized void createOrUpdate(TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(request.getId())
            .orElseGet(() -> TestCase.builder()
                .id(request.getId())
                .name(request.getName())
                .build());

        testCase.setGroups(objectMapper.writeValueAsString(request.getGroups()));
        testCase.setLastRun(dateTimeUtil.getCurrentOffsetDateTime());

        testCaseRepository.save(testCase);
    }
}
