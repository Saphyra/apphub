package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.MenuOrder;
import com.github.saphyra.apphub.ci.menu.MenuOrderEnum;
import com.github.saphyra.apphub.ci.menu.settings.SettingsMenu;
import com.github.saphyra.apphub.ci.utils.ApplicationContextProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuSettingsMenuOption implements MenuOption {
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public Menu getMenu() {
        return Menu.MAIN_MENU;
    }

    @Override
    public MenuOrder getOrder() {
        return MenuOrderEnum.MAIN_MENU_SETTINGS;
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.SETTINGS;
    }

    @Override
    public boolean process() {
        applicationContextProxy.getBean(SettingsMenu.class).enter();

        return false;
    }
}
