package com.github.saphyra.apphub.ci.menu.production;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.process.minikube.production.ProductionReleaseProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ProductionReleaseMenuOption implements MenuOption {
    private final ProductionReleaseProcess productionReleaseProcess;

    @Override
    public Menu getMenu() {
        return Menu.PRODUCTION_MENU;
    }

    @Override
    public MenuOrderEnum getOrder() {
        return MenuOrderEnum.DEPLOY;
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PRODUCTION_RELEASE;
    }

    @Override
    public boolean process() {
        productionReleaseProcess.release();

        return false;
    }
}
