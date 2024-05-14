package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunEditPropertiesMenu extends MenuBase<LocalRunEditPropertiesMenuOption> {
    LocalRunEditPropertiesMenu(List<LocalRunEditPropertiesMenuOption> localRunEditPropertiesMenuOptions) {
        super(localRunEditPropertiesMenuOptions);
    }

    @Override
    protected String getName() {
        return "Which property would you like to edit?"; //TODO translate
    }
}
