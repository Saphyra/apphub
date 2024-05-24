package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.production.ProductionMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuProductionMenuOption implements MainMenuOption {
    private final ProductionMenu productionMenu;

    @Override
    public String getCommand() {
        return "4";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.PRODUCTION;
    }

    @Override
    public boolean process() {
        productionMenu.enter();

        return false;
    }
}
