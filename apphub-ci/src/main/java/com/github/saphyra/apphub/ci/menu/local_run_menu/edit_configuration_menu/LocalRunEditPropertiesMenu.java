package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizationService;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunEditPropertiesMenu extends MenuBase<LocalRunEditPropertiesMenuOption> {
    LocalRunEditPropertiesMenu(List<LocalRunEditPropertiesMenuOption> localRunEditPropertiesMenuOptions, LocalizationService localizationService) {
        super(localRunEditPropertiesMenuOptions, localizationService);
    }

    @Override
    protected LocalizedText getName() {
        return LocalizedText.WHICH_PROPERTY_TO_EDIT;
    }
}
