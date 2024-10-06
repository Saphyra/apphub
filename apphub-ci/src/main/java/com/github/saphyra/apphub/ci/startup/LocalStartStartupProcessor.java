package com.github.saphyra.apphub.ci.startup;

import com.github.saphyra.apphub.ci.process.local.start.LocalStartProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalStartStartupProcessor implements StartupProcessor {
    private static final String LOCAL_START_COMMAND = "local_start";

    private final LocalStartProcess localStartProcess;

    @Override
    public boolean canProcess(String arg) {
        return LOCAL_START_COMMAND.equals(arg);
    }

    @Override
    public void process(List<String> args) {
        localStartProcess.run();
    }
}
