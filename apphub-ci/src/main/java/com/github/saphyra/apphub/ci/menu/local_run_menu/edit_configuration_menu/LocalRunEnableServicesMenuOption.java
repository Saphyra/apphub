package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.enable_services.LocalRunEnableServicesMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEnableServicesMenuOption implements MenuOption {
    private final LocalRunEnableServicesMenu localRunEnableServicesMenu;

    @Override
    public Menu getMenu() {
        return Menu.LOCAL_RUN_EDIT_CONFIGURATION_MENU;
    }

    @Override
    public String getCommand() {
        return "5";
    }

    @Override
    public LocalizationProvider getName() {
        return LocalizedText.ENABLE_SERVICES;
    }

    @Override
    public boolean process() {
        localRunEnableServicesMenu.enter();

        return false;
    }
}
