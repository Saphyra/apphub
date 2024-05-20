package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.run_tests.LocalRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunTestsMenuOption implements LocalRunMenuOption {
    private final LocalRunTestsProcess localRunTestsProcess;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.RUN_TESTS;
    }

    @Override
    public boolean process() {
        localRunTestsProcess.run();

        return false;
    }
}
