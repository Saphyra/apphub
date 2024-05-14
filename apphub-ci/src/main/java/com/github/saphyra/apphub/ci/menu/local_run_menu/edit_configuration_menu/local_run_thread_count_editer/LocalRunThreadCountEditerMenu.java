package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_thread_count_editer;

import com.github.saphyra.apphub.ci.menu.MenuBase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalRunThreadCountEditerMenu extends MenuBase<LocalRunThreadCountEditerMenuOption> {
    LocalRunThreadCountEditerMenu(List<LocalRunThreadCountEditerMenuOption> localRunThreadCountEditerMenuOptions) {
        super(localRunThreadCountEditerMenuOptions);
    }

    @Override
    protected String getName() {
        return "Thread count"; //TODO translate
    }
}
