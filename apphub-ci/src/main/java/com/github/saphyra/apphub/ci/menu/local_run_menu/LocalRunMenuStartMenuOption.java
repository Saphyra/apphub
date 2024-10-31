package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.local.start.LocalStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuStartMenuOption implements MenuOption {
    private final LocalStartProcess localStartProcess;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.DEPLOY;
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
