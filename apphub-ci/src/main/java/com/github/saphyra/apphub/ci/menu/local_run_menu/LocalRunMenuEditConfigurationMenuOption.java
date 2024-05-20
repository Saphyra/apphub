package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.LocalRunEditConfigurationMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunMenuEditConfigurationMenuOption implements LocalRunMenuOption {
    private final LocalRunEditConfigurationMenu localRunEditConfigurationMenu;

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
        localRunEditConfigurationMenu.enter();

        return false;
    }
}
