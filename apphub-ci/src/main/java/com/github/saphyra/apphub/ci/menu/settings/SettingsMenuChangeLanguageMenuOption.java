package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import com.github.saphyra.apphub.ci.menu.settings.change_language.ChangeLanguageMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SettingsMenuChangeLanguageMenuOption implements MenuOption {
    private final ChangeLanguageMenu changeLanguageMenu;

    @Override
    public Menu getMenu() {
        return Menu.SETTINGS_MENU;
    }

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.CHANGE_LANGUAGE;
    }

    @Override
    public boolean process() {
        changeLanguageMenu.enter();

        return false;
    }
}
