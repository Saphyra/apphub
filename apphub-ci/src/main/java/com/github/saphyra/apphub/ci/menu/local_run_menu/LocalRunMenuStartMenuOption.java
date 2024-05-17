package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
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
    public LocalizedText getName() {
        return LocalizedText.START;
    }

    @Override
    public boolean process() {
        localStartProcess.run();

        return false;
    }
}
