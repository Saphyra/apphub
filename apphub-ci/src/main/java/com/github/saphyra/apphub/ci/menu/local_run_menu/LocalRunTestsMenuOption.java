package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunTestsMenuOption implements LocalRunMenuOption{
    private final LocalRunTestsProcess localRunTestsProcess;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public String getName() {
        return "Run tests"; //TODO translate
    }

    @Override
    public boolean process() {
        localRunTestsProcess.run();

        return false;
    }
}
