package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunMenu extends MenuBase {
    LocalRunMenu(List<MenuOption> menuOptions, LocalizationService localizationService) {
        super(menuOptions, localizationService, Menu.LOCAL_RUN_MENU);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.LOCAL_RUN;
    }
}
