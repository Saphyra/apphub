package com.github.saphyra.apphub.ci.service.run_test;

import com.github.saphyra.apphub.ci.service.run_test.local.LocalTestService;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
public class RunTestFacade {
    private final LocalTestService localTestService;

    public void runTests(Environment environment, String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        switch (environment) {
            case LOCAL -> localTestService.runTests(testFilter, threadCount, preCreatedDriverCount, retryCount);
            default -> throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
    }
}
