package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MinikubeEditConfigurationMenu extends MenuBase {
    public MinikubeEditConfigurationMenu(List<MenuOption> menuOptions, LocalizationService localizationService) {
        super(menuOptions, localizationService, Menu.MINIKUBE_EDIT_CONFIGURATION_MENU);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.WHICH_CONFIGURATION_TO_EDIT;
    }
}
