package com.github.saphyra.apphub.ci.startup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ExitStartupProcessor implements StartupProcessor {
    private static final String EXIT_COMMAND = "exit";

    @Override
    public boolean canProcess(String arg) {
        return EXIT_COMMAND.equals(arg);
    }

    @Override
    public void process(List<String> args) {
        System.exit(0);
    }
}
