package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
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
    public String getName() {
        return String.format("Mode (%s)", propertyDao.getLocalRunMode()); //TODO translate
    }

    @Override
    public boolean process() {
        localRunModeSelectorMenu.enter();

        return false;
    }
}
