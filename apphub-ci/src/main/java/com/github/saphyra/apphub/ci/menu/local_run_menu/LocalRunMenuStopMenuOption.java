package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStopMenuOption implements LocalRunMenuOption {
    private final LocalStopProcess localStopProcess;

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.STOP;
    }

    @Override
    public boolean process() {
        localStopProcess.stopServices();

        return false;
    }
}
