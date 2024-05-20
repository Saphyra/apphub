package com.github.saphyra.apphub.ci.menu.minikube.edit_configuration_menu;

import com.github.saphyra.apphub.ci.localization.LocalizedText;
import com.github.saphyra.apphub.ci.menu.thread_count_editer.ThreadCountEditerMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MinikubeEditBuildThreadCountMenuOption implements MinikubeEditConfigurationMenuOption {
    private final ThreadCountEditerMenu threadCountEditerMenu;

    @Override
    public String getCommand() {
        return "1";
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
