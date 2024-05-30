package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStopMenuOption implements MenuOption {
    private final LocalStopProcess localStopProcess;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_MENU;
    }

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
