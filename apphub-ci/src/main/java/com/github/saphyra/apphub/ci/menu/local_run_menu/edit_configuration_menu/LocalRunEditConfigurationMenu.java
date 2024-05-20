package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunEditConfigurationMenu extends MenuBase<LocalRunEditConfigurationMenuOption> {
    LocalRunEditConfigurationMenu(List<LocalRunEditConfigurationMenuOption> localRunEditConfigurationMenuOptions, LocalizationService localizationService) {
        super(localRunEditConfigurationMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.WHICH_CONFIGURATION_TO_EDIT;
    }
}
