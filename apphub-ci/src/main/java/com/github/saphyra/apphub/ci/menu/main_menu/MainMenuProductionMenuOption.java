package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.production.ProductionMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuProductionMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        return Menu.MAIN_MENU;
    }

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
        applicationContextProxy.getBean(ProductionMenu.class).enter();

        return false;
    }
}
