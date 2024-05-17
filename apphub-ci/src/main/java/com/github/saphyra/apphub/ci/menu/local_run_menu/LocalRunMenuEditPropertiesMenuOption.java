package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.LocalRunEditPropertiesMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuEditPropertiesMenuOption implements LocalRunMenuOption {
    private final LocalRunEditPropertiesMenu localRunEditPropertiesMenu;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizedText getName() {
        return LocalizedText.EDIT_CONFIGURATION;
    }

    @Override
    public boolean process() {
        localRunEditPropertiesMenu.enter();

        return false;
    }
}
