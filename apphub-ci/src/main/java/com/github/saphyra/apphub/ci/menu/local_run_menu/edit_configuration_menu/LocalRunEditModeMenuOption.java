package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.localization.LocalizationProvider;
import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector.LocalRunModeSelectorMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEditModeMenuOption implements LocalRunEditPropertiesMenuOption {
    private final LocalRunModeSelectorMenu localRunModeSelectorMenu;
    private final PropertyDao propertyDao;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public LocalizationProvider getName() {
        return language -> LocalizedText.LOCAL_RUN_MODE.getLocalizedText(language).formatted(propertyDao.getLocalRunMode().getLocalization().getLocalizedText(language));
    }

    @Override
    public boolean process() {
        localRunModeSelectorMenu.enter();

        return false;
    }
}
