package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.settings.SettingsMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MainMenuSettingsMenuOption implements MainMenuOption {
    private final SettingsMenu settingsMenu;

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.SETTINGS;
    }

    @Override
    public boolean process() {
        settingsMenu.enter();

        return false;
    }
}
