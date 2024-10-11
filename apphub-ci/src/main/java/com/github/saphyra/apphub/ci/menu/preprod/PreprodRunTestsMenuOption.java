package com.github.saphyra.apphub.ci.menu.preprod;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.preprod.PreprodRunTestsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PreprodRunTestsMenuOption implements MenuOption {
    private final PreprodRunTestsProcess preprodRunTestsProcess;

    @Override
    public Menu getMenu() {
        return Menu.PREPROD_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.RUN_TESTS;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.RUN_TESTS;
    }

    @Override
    public boolean process() {
        preprodRunTestsProcess.runTests();

        return false;
    }
}
