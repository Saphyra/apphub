package com.github.saphyra.apphub.ci.menu.local_run_menu;

import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunMenu extends MenuBase<LocalRunMenuOption> {
    LocalRunMenu(List<LocalRunMenuOption> localRunMenuOptions) {
        super(localRunMenuOptions);
    }

    @Override
    protected String getName() {
        return "Local Start"; //TODO unit test
    }
}
