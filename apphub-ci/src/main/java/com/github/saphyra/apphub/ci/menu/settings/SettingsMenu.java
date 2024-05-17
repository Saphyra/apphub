package com.github.saphyra.apphub.ci.menu.settings;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingsMenu extends MenuBase<SettingsMenuOption> {
    SettingsMenu(List<SettingsMenuOption> settingsMenuOptions, LocalizationService localizationService) {
        super(settingsMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.SETTINGS;
    }
}
