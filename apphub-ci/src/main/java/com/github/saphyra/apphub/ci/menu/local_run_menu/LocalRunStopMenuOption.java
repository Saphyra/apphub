package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunStopMenuOption implements MenuOption {
    private final LocalStopProcess localStopProcess;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.STOP;
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.STOP;
    }

    @Override
    public boolean process() {
        localStopProcess.stopAllServices();

        return false;
    }
}
