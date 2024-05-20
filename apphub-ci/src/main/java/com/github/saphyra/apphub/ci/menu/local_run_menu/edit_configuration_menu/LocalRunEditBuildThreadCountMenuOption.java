package com.github.saphyra.apphub.ci.menu.local_run_menu.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.thread_count_editer.ThreadCountEditerMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LocalRunEditBuildThreadCountMenuOption implements LocalRunEditConfigurationMenuOption {
    private final ThreadCountEditerMenu threadCountEditerMenu;

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
        threadCountEditerMenu.enter();

        return false;
    }
}
