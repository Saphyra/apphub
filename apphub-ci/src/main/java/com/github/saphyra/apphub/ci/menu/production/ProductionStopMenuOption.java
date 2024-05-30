package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionStopProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionStopMenuOption implements MenuOption {
    private final ProductionStopProcess productionStopProcess;

    @Override
    public Menu getMenu() {
        return Menu.PRODUCTION_MENU;
    }

    @Override
    public String getCommand() {
        return "3";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.MINIKUBE_STOP;
    }

    @Override
    public boolean process() {
        productionStopProcess.stopMinikube();

        return false;
    }
}
