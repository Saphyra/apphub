package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.value.DefaultProperties;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.dao.PropertyRepository;
import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector.LocalRunModeSelectorMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEditModeMenuOption implements LocalRunEditPropertiesMenuOption {
    private final LocalRunModeSelectorMenu localRunModeSelectorMenu;
    private final PropertyRepository propertyRepository;
    private final DefaultProperties defaultProperties;

    @Override
    public String getCommand() {
        return "1";
    }

    @Override
    public String getName() {
        return String.format("Mode (%s)", propertyRepository.findById(PropertyName.LOCAL_RUN_MODE).map(property -> LocalRunMode.valueOf(property.getValue())).orElse(defaultProperties.getLocalRunMode())); //TODO translate
    }

    @Override
    public boolean process() {
        localRunModeSelectorMenu.enter();

        return false;
    }
}
