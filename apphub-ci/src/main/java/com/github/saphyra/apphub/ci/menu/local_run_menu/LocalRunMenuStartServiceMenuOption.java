package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.process.local.start.LocalStartServicesProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStartServiceMenuOption implements LocalRunMenuOption {
    private final LocalStartServicesProcess localStartServicesProcess;

    @Override
    public String getCommand() {
        return "6";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.LOCAL_START_SERVICE;
    }

    @Override
    public boolean process() {
        localStartServicesProcess.start();

        return false;
    }
}
