package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingsMenu extends MenuBase {
    SettingsMenu(List<MenuOption> menuOptions, LocalizationService localizationService) {
        super(menuOptions, localizationService, Menu.SETTINGS_MENU);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.SETTINGS;
    }
}
