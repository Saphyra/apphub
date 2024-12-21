package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionStartProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionStartMenuOption implements MenuOption {
    private final ProductionStartProcess productionStartProcess;

    @Override
    public Menu getMenu() {
        return Menu.PRODUCTION_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.START_VM;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_START;
    }

    @Override
    public boolean process() {
        productionStartProcess.startServer();

        return false;
    }
}
