package com.github.saphyra.apphub.ci.menu.main_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainMenu extends MenuBase<MainMenuOption> {
    MainMenu(List<MainMenuOption> mainMenuOptions, LocalizationService localizationService) {
        super(mainMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.MAIN_MENU;
    }
}
