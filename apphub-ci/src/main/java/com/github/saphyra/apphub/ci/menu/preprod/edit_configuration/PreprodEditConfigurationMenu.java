package com.github.saphyra.apphub.ci.menu.preprod.edit_configuration;

import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.Menu;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import com.github.saphyra.apphub.ci.menu.MenuOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreprodEditConfigurationMenu extends MenuBase {
    public PreprodEditConfigurationMenu(List<MenuOption> options, LocalizationService localizationService) {
        super(options, localizationService, Menu.PREPROD_EDIT_CONFIGURATION_MENU);
    }

    @Override
    protected LocalizationProvider getName() {
        return LocalizedText.EDIT_CONFIGURATION;
    }
}
