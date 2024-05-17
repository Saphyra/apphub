package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
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
    public LocalizedText getName() {
        return LocalizedText.BUILD_THREAD_COUNT;
    }

    @Override
    public boolean process() {
        localRunThreadCountEditerMenu.enter();

        return false;
    }
}
