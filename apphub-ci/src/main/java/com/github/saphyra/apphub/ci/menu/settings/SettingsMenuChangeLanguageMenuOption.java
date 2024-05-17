package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.settings.change_language.ChangeLanguageMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SettingsMenuChangeLanguageMenuOption implements SettingsMenuOption {
    private final ChangeLanguageMenu changeLanguageMenu;

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
