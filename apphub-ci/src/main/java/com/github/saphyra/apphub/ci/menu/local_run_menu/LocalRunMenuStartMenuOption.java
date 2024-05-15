package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.process.local.start.LocalStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStartMenuOption implements LocalRunMenuOption {
    private final LocalStartProcess localStartProcess;

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public String getName() {
        return "Start"; //TODO translate
    }

    @Override
    public boolean process() {
        localStartProcess.run();

        return false;
    }
}
