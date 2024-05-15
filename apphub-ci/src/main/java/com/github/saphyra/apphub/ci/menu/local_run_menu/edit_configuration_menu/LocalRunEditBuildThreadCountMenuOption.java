package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu.local_run_thread_count_editer.LocalRunThreadCountEditerMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEditBuildThreadCountMenuOption implements LocalRunEditPropertiesMenuOption {
    private final LocalRunThreadCountEditerMenu localRunThreadCountEditerMenu;

    @Override
    public String getCommand() {
        return "2";
    }

    @Override
    public String getName() {
        return "Build Thread Count"; //TODO translate
    }

    @Override
    public boolean process() {
        localRunThreadCountEditerMenu.enter();

        return false;
    }
}
