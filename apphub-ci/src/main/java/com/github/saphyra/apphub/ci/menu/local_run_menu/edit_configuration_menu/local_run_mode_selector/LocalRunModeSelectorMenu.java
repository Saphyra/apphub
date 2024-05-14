package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_mode_selector;

import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunModeSelectorMenu extends MenuBase<LocalRunModeSelectorOption> {
    LocalRunModeSelectorMenu(List<LocalRunModeSelectorOption> localRunModeSelectorOptions) {
        super(localRunModeSelectorOptions);
    }

    @Override
    protected String getName() {
        return "Select mode"; //TODO translate
    }
}
